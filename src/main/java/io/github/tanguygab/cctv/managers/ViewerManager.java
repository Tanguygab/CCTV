package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.ViewerOptionsMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ViewerManager extends Manager<Viewer> {

    public boolean CAN_CHAT;
    public boolean ZOOM_ITEM;
    public boolean SPOTTING;
    public boolean BOSSBAR;

    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;
    public final List<String> blockedCommands = new ArrayList<>();

    private final CameraManager cm = cctv.getCameras();
    public final Map<UUID, Location> viewersQuit = new HashMap<>();

    public ViewerManager() {
        super("userdata.yml");
    }

    @Override
    public void load() {
        ConfigurationFile config = cctv.getConfiguration();
        CAN_CHAT = config.getBoolean("viewers.can_chat",true);
        ZOOM_ITEM = cctv.getConfiguration().getBoolean("camera.zoom_item",true);
        SPOTTING = cctv.getConfiguration().getBoolean("camera.spotting",true);
        TIME_TO_CONNECT = config.getInt("viewers.timed-actions.connect",3);
        TIME_TO_DISCONNECT = config.getInt("viewers.timed-actions.disconnect",3);
        TIME_FOR_SPOT = config.getInt("viewers.timed-actions.spot",5);
        BOSSBAR = config.getBoolean("viewers.bossbar",true);
        blockedCommands.addAll(config.getStringList("viewers.blocked-commands",List.of()));

        Map<String,Object> loggedOutViewers = file.getConfigurationSection("logged-out-viewers");
        loggedOutViewers.keySet().forEach(this::loadFromConfig);
        file.set("logged-out-viewers",null);
    }

    @Override
    public void unload() {
        values().forEach(v-> cctv.getCameras().disconnectFromCamera(v.getPlayer()));
        viewersQuit.forEach((uuid,loc)->{
            Map<String,Object> locMap = new HashMap<>();
            locMap.put("world",loc.getWorld() != null ? loc.getWorld().getName() : "");
            locMap.put("x",loc.getX());
            locMap.put("y",loc.getY());
            locMap.put("z",loc.getZ());
            locMap.put("pitch",loc.getPitch());
            locMap.put("yaw",loc.getYaw());
            file.set("logged-out-viewers."+uuid,locMap);
        });
    }

    @Override
    protected void loadFromConfig(String uuid) {
        viewersQuit.put(UUID.fromString(uuid),Utils.loadLocation("logged-out-viewers."+uuid,file));
    }

    @Override
    protected void saveToConfig(Viewer value) {}

    @SuppressWarnings("UnstableApiUsage")
    public void delete(Player p) {
        Viewer viewer = get(p);
        viewer.setCamera(null,false);
        if (!cm.EXPERIMENTAL_VIEW)
            for (Player online : Bukkit.getOnlinePlayers())
                online.showPlayer(cctv,p);

        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.setCanPickupItems(true);
        p.showEntity(cctv, viewer.getCamera().getArmorStand());
        remove(viewer.getUuid().toString());
    }
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }

    public boolean exists(Player p) {
        return exists(p.getUniqueId().toString());
    }

    public void createPlayer(Player p, Computable camera, Computer computer) {
        Viewer viewer = new Viewer(p,camera,computer);
        put(viewer.getUuid().toString(),viewer);

        p.setCanPickupItems(false);
        giveViewerItems(p,computer);

        if (!cm.EXPERIMENTAL_VIEW)
            for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(cctv,p);
    }

    private void giveViewerItems(Player p, Computer computer) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setItem(0, CCTVMenu.getItem(Heads.OPTIONS,lang.CAMERA_VIEW_OPTION));
        inv.setItem(3, Heads.ROTATE_LEFT.get());
        inv.setItem(computer != null && computer.getCameras().size() > 1 ? 4 : 5, Heads.ROTATE_RIGHT.get());
        inv.setItem(6, Heads.CAM_PREVIOUS.get());
        inv.setItem(7, Heads.CAM_NEXT.get());
        inv.setItem(8, CCTVMenu.getItem(Heads.EXIT,lang.CAMERA_VIEW_EXIT));
    }

    public void onCameraItems(Player p, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String itemName = item.getItemMeta().getDisplayName();
        if (itemName.equals(lang.CAMERA_VIEW_EXIT)) {
            p.sendTitle(" ", cctv.getLang().CAMERA_DISCONNECTING, 0, TIME_TO_DISCONNECT*20, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> cm.disconnectFromCamera(p),  TIME_TO_DISCONNECT * 20L);
            return;
        }
        if (itemName.equals(lang.CAMERA_VIEW_OPTION)) cctv.openMenu(p,new ViewerOptionsMenu(p));
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_LEFT)) cm.rotate(p,get(p).getCamera(), -18,true);
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_RIGHT)) cm.rotate(p,get(p).getCamera(), 18,true);
        if (itemName.equals(lang.CAMERA_VIEW_PREVIOUS)) switchCamera(p,true);
        if (itemName.equals(lang.CAMERA_VIEW_NEXT)) switchCamera(p,false);
    }

    public void switchCamera(Player player, boolean previous) {
        if (!player.hasPermission("cctv.view.switch")) {
            player.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(player);
        Computer computer = viewer.getComputer();
        if (computer == null) {
            player.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
            return;
        }

        List<Computable> cams = new ArrayList<>(computer.getCameras());
        if (cams.isEmpty() || cams.size() == 1 && cams.get(0) instanceof Camera) {
            player.sendMessage(lang.NO_CAMERAS);
            return;
        }

        Computable group = viewer.getGroup();
        Camera camera = viewer.getCamera();
        int tries = 0;
        do {
            tries++;
            group = getNextCamera(camera,group,previous,cams);
            camera = group.get(camera);
        } while (!group.available(camera,previous) && tries < 10);

        if (group.get(camera,previous) == null) return;
        viewer.setCamera(group,previous);
    }

    public Computable getNextCamera(Camera camera, Computable current, boolean previous, List<Computable> cameras) {
        if (!current.next(camera,previous)) return current;
        int index = cameras.indexOf(current);
        if (previous) {
            if (index == 0) index = cameras.size();
            return cameras.get(--index);
        }
        if (index == (cameras.size()-1)) index = -1;
        return cameras.get(++index);

    }

}
