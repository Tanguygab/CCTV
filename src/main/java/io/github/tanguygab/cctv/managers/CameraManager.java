package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.*;

public class CameraManager extends Manager<Camera> {

    public boolean EXPERIMENTAL_VIEW;
    private final Map<String,List<String>> unloadedWorlds = new HashMap<>();
    public List<Player> connecting = new ArrayList<>();

    public CameraManager() {
        super("cameras.yml");
    }

    @Override
    public void load() {
        EXPERIMENTAL_VIEW = cctv.getConfiguration().getBoolean("camera.experimental_view",false);

        if (EXPERIMENTAL_VIEW && !cctv.getNms().isNMSSupported()) {
            EXPERIMENTAL_VIEW = false;
            cctv.getLogger().severe("Experimental View is enabled but your server doesn't support it! Switching back to normal view.");
        }

        file.getValues().keySet().forEach(id->{
            String w = file.getString(id+".world");
            World world = Bukkit.getServer().getWorld(w);
            if (world == null) {
                unloadedWorlds.computeIfAbsent(w.toLowerCase(),wo->new ArrayList<>()).add(id);
                return;
            }
            loadFromConfig(id);
        });
    }

    private void loadFromConfig(String id) {
        String owner = file.getString(id+".owner");
        String skin = file.getString(id+".skin", "_DEFAULT_");
        boolean enabled = file.getBoolean(id+".enabled",true);
        boolean shown = file.getBoolean(id+".shown",true);

        Location loc = Utils.loadLocation(id,file);

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
        if (cam.getCreeper() != null) cam.getCreeper().remove();
        player.sendMessage(lang.CAMERA_DELETE+"\n"+lang.getCameraID(cam.getId()));
        cctv.getViewers().values().stream().filter(viewer -> viewer.getCamera() == cam).forEach(p -> disconnectFromCamera(Bukkit.getPlayer(p.getId())));
        cctv.getComputers().values().forEach(computer->computer.removeCamera(cam));
        delete(cam.getId());
        Utils.giveOrDrop(player,cctv.getCustomHeads().get(cam.getSkin()));
    }

    public void create(String id, String owner, Location loc, boolean enabled, boolean shown, String skin, boolean isLoaded) {
        if (exists(id)) return;
        ArmorStand as = null;
        Creeper creeper = null;
        if (isLoaded) {
            as = (ArmorStand) spawnEntity(id,loc.clone(),EntityType.ARMOR_STAND);
            if (shown) Objects.requireNonNull(as.getEquipment()).setHelmet(Heads.CAMERA.get());
            as.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
            as.setVisible(false);

            if (EXPERIMENTAL_VIEW) {
                creeper = (Creeper) spawnEntity(id,loc.clone().add(0,0.5,0),EntityType.CREEPER);
                creeper.setInvisible(true);
                creeper.setExplosionRadius(0);
            }
        }
        Camera camera = new Camera(id,owner,loc,enabled,shown,as,creeper,skin);
        put(id,camera);
    }

    private LivingEntity spawnEntity(String id, Location loc, EntityType entityType) {
        LivingEntity entity = (LivingEntity) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, entityType);
        entity.setCustomName("CAM-"+id);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setSilent(true);
        entity.setCollidable(false);
        entity.setAI(false);
        return entity;
    }

    public void create(String id, Location loc, Player player, String skin) {
        if (id == null) id = getRandomID();
        if (id.contains(".")) {
            player.sendMessage(lang.DOT_IN_ID);
            return;
        }
        if (exists(id)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }

        create(id,player.getUniqueId().toString(),loc,true,true,skin,true);
        player.sendMessage(lang.CAMERA_CREATE+"\n"+lang.getCameraID(id));
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
            connecting.remove(p);
        }, vm.TIME_TO_CONNECT * 20L);
    }

    public void rotate(Player p, Camera camera, int degrees, boolean horizontal) {
        if (!p.hasPermission("cctv.view.move")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        boolean rotate =  horizontal ? camera.rotateHorizontally(degrees) : camera.rotateVertically(degrees);
        if (!rotate) {
            p.sendMessage(lang.MAX_ROTATION);
            return;
        }
        if (!EXPERIMENTAL_VIEW && cctv.getViewers().exists(p)) p.teleport(camera.getArmorStand().getLocation());
    }

    public void loadWorld(World world) {
        String w = world.getName().toLowerCase();
        if (unloadedWorlds.containsKey(w)) unloadedWorlds.remove(w).forEach(this::loadFromConfig);
    }

    public void createCamera(Player p, ItemStack item, Location loc, BlockFace face) {
        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
            item.setAmount(item.getAmount()-1);

        double x,y,z;
        float yaw;
        switch (face) {
            case UP -> {x=0.5D;z=0.5D;y=0.47D;yaw=loc.getYaw()+180.0F;}
            case DOWN -> {x=0.5D;z=0.5D;y=2.03D;yaw=loc.getYaw()+180.0F;}
            case EAST -> {x=1.29D;z=0.5D;y=1.24D;yaw=270.0F;}
            case WEST -> {x=-0.29D;z=0.5D;y=1.24D;yaw=90.0F;}
            case NORTH -> {x=0.5D;z=-0.29D;y=1.24D;yaw=180.0F;}
            case SOUTH -> {x=0.5D;z=1.29D;y=1.24D;yaw=0.0F;}
            default -> {x=0;z=0;y=0;yaw=0;}
        }
        loc.setX(loc.getX()+x);
        loc.setZ(loc.getZ()+z);
        loc.setY(loc.getY()-y);
        loc.setYaw(yaw);

        create(null, loc, p,CCTV.getInstance().getCustomHeads().get(item));
    }

}
