package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraManager extends Manager<Camera> {

    public boolean EXPERIMENTAL_VIEW;
    public boolean ZOOM_ITEM;
    private final Map<String,List<Map<String,Object>>> unloadedWorlds = new HashMap<>();

    public List<Player> connecting = new ArrayList<>();

    public CameraManager() {
        super("cameras.yml");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        EXPERIMENTAL_VIEW = cctv.getConfiguration().getBoolean("camera.experimental_view",false);
        ZOOM_ITEM = cctv.getConfiguration().getBoolean("camera.zoom_item",true);

        if (EXPERIMENTAL_VIEW && !cctv.getNms().isNMSSupported()) {
            EXPERIMENTAL_VIEW = false;
            cctv.getLogger().severe("Experimental View is enabled but your server doesn't support it! Switching back to normal view.");
        }
        
        Map<String, Object> cams = file.getValues();
        cams.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;

            String w = String.valueOf(config.get("world"));
            World world = Bukkit.getServer().getWorld(w);
            if (world == null) {
                unloadedWorlds.computeIfAbsent(w.toLowerCase(),wo->new ArrayList<>()).add(new HashMap<>(config) {{
                    put("id",id);
                }});
                return;
            }
            loadFromConfig(id,config,world);
        });
    }

    private void loadFromConfig(String id, Map<String,Object> config, World world) {
        String owner = String.valueOf(config.get("owner"));
        String skin = String.valueOf(config.getOrDefault("skin", "_DEFAULT_"));
        boolean enabled = (boolean) config.getOrDefault("enabled",true);
        boolean shown = (boolean) config.getOrDefault("shown",true);

        Location loc = Utils.loadLocation(world,config);

        for (Entity entity : loc.getChunk().getEntities()) {
            if ((entity instanceof ArmorStand || entity instanceof Creeper) && entity.getCustomName() != null && entity.getCustomName().equals("CAM-" + id))
                entity.remove();
        }
        create(id,owner,loc,enabled,shown,skin,loc.getChunk().isLoaded());
    }

    public void unload() {
        map.forEach((id, cam)-> {
            cam.getArmorStand().remove();
            if (cam.getCreeper() != null)
                cam.getCreeper().remove();
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
        if (cam.getCreeper() != null)
            cam.getCreeper().remove();
        player.sendMessage(lang.CAMERA_DELETE);
        player.sendMessage(lang.getCameraID(cam.getId()));
        cctv.getViewers().values().stream().filter(viewer -> viewer.getCamera() == cam).forEach(p -> disconnectFromCamera(Bukkit.getPlayer(p.getId())));
        cctv.getComputers().values().forEach(computer->computer.removeCamera(cam));
        delete(cam.getId());
        if (player.getGameMode() == GameMode.SURVIVAL) player.getInventory().addItem(cctv.getCustomHeads().get(cam.getSkin()));
    }

    public void create(String id, String owner, Location loc, boolean enabled, boolean shown, String skin, boolean isLoaded) {
        if (exists(id)) return;
        ArmorStand as = null;
        Creeper creeper = null;
        if (isLoaded) {
            as = loc.getWorld().spawn(loc, ArmorStand.class);
            as.setGravity(false);
            as.setCollidable(false);
            as.setInvulnerable(true);
            as.setVisible(false);
            as.setCustomName("CAM-" + id);
            as.setSilent(true);
            as.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
            if (shown) as.getEquipment().setHelmet(Heads.CAMERA.get());

            if (EXPERIMENTAL_VIEW) {
                loc.add(0, 0.5, 0);
                creeper = loc.getWorld().spawn(loc, Creeper.class);
                loc.add(0, -0.5, 0);
                creeper.setCustomName("CAM-" + id);
                creeper.setInvisible(true);
                creeper.setAI(false);
                creeper.setInvulnerable(true);
                creeper.setGravity(false);
                creeper.setSilent(true);
                creeper.setCollidable(false);
                creeper.setExplosionRadius(0);
            }
        }
        Camera camera = new Camera(id,owner,loc,enabled,shown,as,creeper,skin);
        put(id,camera);
    }

    public void create(String id, Location loc, Player player, String skin) {
        if (id != null && id.contains(".")) {
            player.sendMessage(lang.DOT_IN_ID);
            return;
        }
        if (exists(id)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }
        if (id == null) id = String.valueOf(Utils.getRandomNumber(999999, this));

        create(id,player.getUniqueId().toString(),loc,true,true,skin,true);
        player.sendMessage(lang.CAMERA_CREATE);
        player.sendMessage(lang.getCameraID(id));
    }


    public void disconnectFromCamera(Player player) {
        if (player == null) return;
        if (!cctv.getViewers().exists(player)) return;
        cctv.getViewers().delete(player);
    }

    public List<String> get(Player p) {
        List<String> cameras = new ArrayList<>();
        for (Camera camera : values())
            if (camera.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.camera.other"))
                cameras.add(camera.getId());
        return cameras;
    }
    public Camera get(Location loc) {
        for (Camera cam : values())
            if (cam.getLocation().equals(loc))
                return cam;
        return null;
    }

    public void viewCamera(Player p, Camera cam, Computer computer) {
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
        if (EXPERIMENTAL_VIEW && Utils.distance(p.getLocation(),cam.getArmorStand().getLocation()) >= 60) {
            p.sendMessage(lang.CAMERA_TOO_FAR);
            return;
        }

        ViewerManager vm = cctv.getViewers();
        p.sendTitle(" ", lang.CAMERA_CONNECTING, 0, vm.TIME_TO_CONNECT*20, 0);
        connecting.add(p);
        Bukkit.getScheduler().scheduleSyncDelayedTask(cctv,  () -> {
            vm.createPlayer(p, cam, computer);
            cctv.getNms().setCameraPacket(p,cam.getArmorStand());
            connecting.remove(p);
        }, vm.TIME_TO_CONNECT * 20L);
    }

    public void viewCameraInstant(Camera cam, Player p) {
        if (cam == null) {
            p.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (EXPERIMENTAL_VIEW && Utils.distance(p.getLocation(),cam.getArmorStand().getLocation()) >= 60) {
            p.sendMessage(lang.CAMERA_TOO_FAR);
            return;
        }
        cctv.getNms().setCameraPacket(p,cam.getArmorStand());
    }

    public void rotateHorizontally(Player p, Camera camera, int degrees) {
        if (!p.hasPermission("cctv.view.move")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        if (!camera.rotateHorizontally(degrees)) {
            p.sendMessage(lang.MAX_ROTATION);
            return;
        }
        if (!EXPERIMENTAL_VIEW && cctv.getViewers().exists(p)) p.teleport(camera.getArmorStand().getLocation());
    }
    public void rotateVertically(Player p, Camera camera, int degrees) {
        if (!p.hasPermission("cctv.view.move")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        if (!camera.rotateVertically(degrees)) {
            p.sendMessage(lang.MAX_ROTATION);
            return;
        }
        if (!EXPERIMENTAL_VIEW && cctv.getViewers().exists(p)) p.teleport(camera.getArmorStand().getLocation());
    }

    public void loadWorld(World world) {
        String w = world.getName().toLowerCase();
        if (!unloadedWorlds.containsKey(w)) return;
        unloadedWorlds.get(w).forEach(cfg->loadFromConfig(String.valueOf(cfg.get("id")),cfg,world));
        unloadedWorlds.remove(w);
    }

    public void createCamera(Player p, ItemStack item, Location loc, BlockFace face) {
        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
            item.setAmount(item.getAmount()-1);
        switch (face) {
            case UP -> setLoc(loc,0.5D,0.5D,0.47D,loc.getYaw()+180.0F);
            case DOWN -> setLoc(loc,0.5D,0.5D,2.03D,loc.getYaw()+180.0F);
            case EAST -> setLoc(loc,1.29D,0.5D,1.24D,270.0F);
            case WEST -> setLoc(loc,-0.29D,0.5D,1.24D,90.0F);
            case NORTH -> setLoc(loc,0.5D,-0.29D,1.24D,180.0F);
            case SOUTH -> setLoc(loc,0.5D,1.29D,1.24D,0.0F);
        }
        create(null, loc, p,CCTV.getInstance().getCustomHeads().get(item));
    }

    private void setLoc(Location loc, double x, double z, double y, float yaw) {
        loc.setX(loc.getX()+x);
        loc.setZ(loc.getZ()+z);
        loc.setY(loc.getY()-y);
        loc.setYaw(yaw);
    }

}
