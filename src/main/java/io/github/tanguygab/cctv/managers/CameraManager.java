package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.NMSUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraManager extends Manager<Camera> {
    
    public double CAMERA_HEAD_RADIUS;
    public List<Player> connecting = new ArrayList<>();

    public CameraManager() {
        super("cameras.yml");
    }

    @Override
    public void load() {
        CAMERA_HEAD_RADIUS = cctv.getConfiguration().getDouble("camera_head_radius",0.35D);
        
        Map<String, Object> cams = file.getValues();
        cams.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            String skin = config.getOrDefault("skin","_DEFAULT_")+"";
            boolean enabled = (boolean) config.get("enabled");
            boolean shown = (boolean) config.get("shown");

            World world = Bukkit.getServer().getWorld(config.get("world")+"");
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");
            double pitch = (double) config.get("pitch");
            double yaw = (double) config.get("yaw");

            Location loc = new Location(world, x, y, z, (float)yaw, (float)pitch);
            if (!loc.getChunk().isLoaded()) loc.getChunk().load();

            for (Entity entity : loc.getChunk().getEntities()) {
                if (entity instanceof ArmorStand as) {
                    if (as.getCustomName() != null && as.getCustomName().equals("CAM-" + id))
                        as.remove();
                }
            }
            create(id,owner,loc,enabled,shown,skin);
        });
    }

    public void unload() {
        map.forEach((id, cam)-> cam.getArmorStand().remove());
    }

    @Override
    public void delete(String id, Player player) {
        Camera cam = get(id);
        if (cam == null) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        cam.getArmorStand().remove();
        player.sendMessage(lang.CAMERA_DELETE);
        player.sendMessage(lang.getCameraID(cam.getId()));
        cctv.getViewers().values().stream().filter(p -> p.getCamera() == cam).forEach(p -> unviewCamera(Bukkit.getPlayer(p.getId())));
        cctv.getCameraGroups().values().forEach(g->g.removeCamera(cam));
        delete(cam.getId());
        if (player.getGameMode() == GameMode.SURVIVAL) player.getInventory().addItem(Heads.CAMERA.get());
    }

    public void create(String id, String owner, Location loc, boolean enabled, boolean shown, String skin) {
        if (exists(id)) return;
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
        as.setGravity(false);
        as.setCollidable(false);
        as.setInvulnerable(true);
        as.setVisible(false);
        as.setCustomName("CAM-" + id);
        as.setSilent(true);
        as.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
        if (shown) as.getEquipment().setHelmet(Heads.CAMERA.get());
        Camera camera = new Camera(id,owner,loc,enabled,shown,as,skin);
        map.put(id,camera);
    }

    public void create(String id, Location loc, Player player, String skin) {
        LanguageFile lang = cctv.getLang();
        if (exists(id)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }
        if (id == null) id = Utils.getRandomNumber(999999, "camera")+"";

        create(id,player.getUniqueId().toString(),loc,true,true,skin);
        player.sendMessage(lang.CAMERA_CREATE);
        player.sendMessage(lang.getCameraID(id));
    }


    public void unviewCamera(Player player) {
        if (player == null) return;

        ViewerManager vm = cctv.getViewers();
        Viewer p = vm.get(player);
        if (p == null) return;
        NMSUtils.despawnNPC(player,p);
        vm.delete(player);
    }

    public List<String> get(Player p) {
        List<String> cameras = new ArrayList<>();
        for (Camera camera : values()) {
            if (camera.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.camera.other"))
                cameras.add(camera.getId());
        }
        return cameras;
    }
    public Camera get(Location loc) {
        for (Camera cam : values()) {
            if (cam.getLocation().equals(loc)) return cam;
        }
        return null;
    }

    public void viewCamera(Player p, Camera cam, CameraGroup group) {
        LanguageFile lang = cctv.getLang();
        if (cam == null) {
            p.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (connecting.contains(p)) return;
        if (!cam.isEnabled()) {
            if (!p.hasPermission("cctv.camera.view.override") && !p.hasPermission("cctv.admin")) {
                p.sendTitle(lang.CAMERA_OFFLINE, "",0, 15, 0);
                return;
            }
            p.sendMessage(lang.CAMERA_OFFLINE_OVERRIDE);
        }

        ViewerManager vm = cctv.getViewers();
        p.sendTitle(" ", lang.CAMERA_CONNECTING, 0, vm.TIME_TO_CONNECT*20, 0);
        connecting.add(p);

        Bukkit.getScheduler().scheduleSyncDelayedTask(cctv,  () -> {
            vm.createPlayer(p, cam, group);
            NMSUtils.spawnNPC(p, p.getLocation());
            teleport(cam, p);
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 60000000, 0, false, false);
            p.addPotionEffect(invisibility);
            if (group != null && vm.exists(p))
                vm.get(p).setGroup(group);
            connecting.remove(p);
        }, vm.TIME_TO_CONNECT * 20L);
    }

    public void viewCameraInstant(Camera cam, Player p) {
        if (cam == null) {
            CCTV.get().getViewers().delete(p);
            p.sendMessage(CCTV.get().getLang().CAMERA_NOT_FOUND);
            return;
        }
        teleport(cam, p);
    }

    public void teleport(Camera cam, Player player) {
        if (cam == null) return;

        ArmorStand as = cam.getArmorStand();
        Location asLoc = as.getLocation();
        double Degrees_Yaw = as.getEyeLocation().getYaw();
        double Degrees_Pitch = as.getEyeLocation().getPitch();
        double radian_yaw = Math.toRadians(Degrees_Yaw);
        double radian_pitch = Math.toRadians(Degrees_Pitch);
        double radius_head = 0.29D;
        double radius = CAMERA_HEAD_RADIUS;

        double l3 = radius_head * Math.sin(radian_pitch);
        boolean b = Math.abs(Degrees_Yaw) > 90.0D && Math.abs(Degrees_Yaw) <= 270.0D;
        if (b) l3 = -l3;

        double x3 = l3 * Math.sin(radian_yaw);
        double y3 = Math.sqrt(Math.pow(radius_head, 2.0D) - Math.pow(l3, 2.0D));
        double z3 = Math.sqrt(Math.pow(l3, 2.0D) - Math.pow(x3, 2.0D));
        if (Degrees_Pitch < 0.0D) z3 = -z3;
        x3 = -x3;
        if (b) {
            x3 = -x3;
            z3 = -z3;
        }

        double x2 = radius * Math.sin(radian_yaw);
        double z2 = Math.sqrt(Math.pow(radius, 2.0D) - Math.pow(x2, 2.0D));
        double y2 = radius * Math.sin(radian_pitch);
        if (b) z2 = -z2;
        y2 = -y2;
        x2 = -x2;
        Location loc = new Location(asLoc.getWorld(), asLoc.getX() + x2 + x3, asLoc.getY() + 0.115D + y2 + y3 - radius_head, asLoc.getZ() + z2 + z3, asLoc.getYaw(), asLoc.getPitch());
        player.teleport(loc);
    }
}
