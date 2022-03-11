package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.cooldownfunctions;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.NPCUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraManager extends Manager<Camera> {

    public CameraManager() {
        super("cameras.yml");
    }

    @Override
    public void load() {
        Map<String, Object> cams = file.getValues();
        cams.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
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
            create(id, owner, loc, enabled, shown);
        });
    }

    @Override
    public void unload() {
        map.forEach((id, cam)->{
            cam.getArmorStand().remove();
            file.set(id + ".owner", cam.getOwner());
            file.set(id + ".enabled", cam.isEnabled());
            file.set(id + ".shown", cam.isShown());
            Location loc = cam.getLocation();
            file.set(id + ".world", loc.getWorld().getName());
            file.set(id + ".x", loc.getX());
            file.set(id + ".y", loc.getY());
            file.set(id + ".z", loc.getZ());
            file.set(id + ".pitch", loc.getPitch());
            file.set(id + ".yaw", loc.getYaw());
        });
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
        CCTV.get().getViewers().values().stream().filter(p -> p.getCamera().equals(cam)).forEach(p -> unviewCamera(Bukkit.getPlayer(p.getId())));
        CCTV.get().getCameraGroups().removeCamera(cam);
        map.remove(cam.getId());
        if (player.getGameMode() == GameMode.SURVIVAL) {
            ItemStack camItem = Heads.CAMERA_1.get();
            ItemMeta camMeta = camItem.getItemMeta();
            camMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Camera"));
            camItem.setItemMeta(camMeta);
            player.getInventory().addItem(camItem);
        }
    }

    public void create(String id, String owner, Location loc, boolean enabled, boolean shown) {
        if (exists(id)) return;
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
        as.setGravity(false);
        as.setCollidable(false);
        as.setVisible(false);
        as.setCustomName("CAM-" + id);
        as.setSilent(true);
        as.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
        if (shown) as.getEquipment().setHelmet(Heads.CAMERA_1.get());
        Camera camera = new Camera(id,owner,loc,enabled,shown,as);
        map.put(id,camera);
        if (CCTV.get().debug) as.setCustomNameVisible(true);
    }

    public void create(String id, Location loc, Player player) {
        LanguageFile lang = CCTV.get().getLang();
        if (exists(id)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }
        if (id == null) id = Utils.getRandomNumber(999999, "computer")+"";

        create(id,player.getUniqueId().toString(),loc,true,true);
        player.sendMessage(lang.CAMERA_CREATE);
        player.sendMessage(lang.getCameraID(id));
    }


    public void unviewCamera(Player player) {
        if (player == null) return;

        ViewerManager vm = CCTV.get().getViewers();
        Viewer p = vm.get(player);
        if (p == null) return;
        NPCUtils.despawn(player,p.getNpc());
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

    private static final Pattern cameraPattern = Pattern.compile("cctv\\.camera\\.limit\\.(\\d+)");
    public boolean canPlaceCamera(Player player) {
        int max = -1;
        int amount = get(player).size();

        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            Matcher m = cameraPattern.matcher(perm.getPermission());
            if (m.matches())
                max = Math.max(max, Integer.parseInt(m.group(1)));
        }
        return player.isOp() || (player.hasPermission("cctv.camera.limit." + (amount + 1)) || ((max == -1 || amount < max)));
    }

    public void renameCamera(String id, String rename, Player player) {
        LanguageFile lang = CCTV.get().getLang();
        if (!exists(id)) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (exists(rename)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }
        Camera cam = map.get(id);
        cam.setId(rename);
        player.sendMessage(lang.getCameraRenamed(rename));
    }

    public void viewCamera(Player player, String id, CameraGroup group) {
        LanguageFile lang = CCTV.get().getLang();
        Camera cam = get(id);
        if (cam == null) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (cooldownfunctions.isCooldownActive(player)) return;
        if (!cam.isEnabled()) {
            if (!player.hasPermission("cctv.camera.view.override") && !player.hasPermission("cctv.admin")) {
                player.sendTitle(lang.CAMERA_OFFLINE, "",0, 15, 0);
                return;
            }
            player.sendMessage(lang.CAMERA_OFFLINE_OVERRIDE);
        }
        player.sendTitle("", lang.CAMERA_CONNECTING, 0, 15, 0);
        cooldownfunctions.addCoolDown(player, CCTV.get().TIME_TO_CONNECT);

        ViewerManager vm = CCTV.get().getViewers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(),  () -> {
            vm.createPlayer(player, cam, group);
            NPCUtils.spawn(player, player.getLocation());
            camerafunctions.teleportToCamera(id, player);
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 60000000, 0, false, false);
            player.addPotionEffect(invisibility);
            if (group != null && vm.exists(player))
                vm.get(player).setGroup(group);
        }, CCTV.get().TIME_TO_CONNECT * 20L);
    }
}
