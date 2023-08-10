package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.entities.Camera;
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
    public boolean GIWP;

    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;
    public final List<String> blockedCmds = new ArrayList<>();

    private final CameraManager cm = cctv.getCameras();
    public final Map<UUID, Location> viewersQuit = new HashMap<>();

    @Override
    public void load() {
        ConfigurationFile config = cctv.getConfiguration();
        CAN_CHAT = config.getBoolean("viewers.can_chat",true);
        GIWP = config.getBoolean("viewers.get_items_without_permission",true);
        TIME_TO_CONNECT = config.getInt("viewers.timed-actions.connect",3);
        TIME_TO_DISCONNECT = config.getInt("viewers.timed-actions.disconnect",3);
        TIME_FOR_SPOT = config.getInt("viewers.timed-actions.spot",5);
        blockedCmds.addAll(config.getStringList("viewers.blocked-commands",List.of()));

        Map<String,Map<String,Object>> loggedOutViewers = config.getConfigurationSection("logged-out-viewers");
        loggedOutViewers.forEach((uuid,loc)->viewersQuit.put(UUID.fromString(uuid),Utils.loadLocation(null,loc)));
        cctv.getConfiguration().set("logged-out-viewers",null);
    }

    public void unload() {
        values().forEach(v-> cctv.getCameras().disconnectFromCamera(get(v)));
        viewersQuit.forEach((uuid,loc)->{
            Map<String,Object> locMap = new HashMap<>();
            locMap.put("world",loc.getWorld() != null ? loc.getWorld().getName() : "");
            locMap.put("x",loc.getX());
            locMap.put("y",loc.getY());
            locMap.put("z",loc.getZ());
            locMap.put("pitch",loc.getPitch());
            locMap.put("yaw",loc.getYaw());
            cctv.getConfiguration().set("logged-out-viewers."+uuid,locMap);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public void delete(Player p) {
        Viewer viewer = get(p);
        cctv.getNms().setCameraPacket(p,p);
        p.getInventory().setContents(viewer.getInv());
        if (!cm.EXPERIMENTAL_VIEW)
            for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(cctv,p);

        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.setCanPickupItems(true);
        p.showEntity(cctv, viewer.getCamera().getArmorStand());
        delete(viewer.getId());
    }
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }

    public Player get(Viewer viewer) {
        return Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
    }

    public boolean exists(Player p) {
        return exists(p.getUniqueId().toString());
    }

    public void createPlayer(Player p, Camera cam, Computer computer) {
        Viewer viewer = new Viewer(p,cam,computer);
        put(viewer.getId(),viewer);

        p.setCanPickupItems(false);
        giveViewerItems(p,computer);

        if (!cm.EXPERIMENTAL_VIEW)
            for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(cctv,p);
    }

    private void giveViewerItems(Player p, Computer computer) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (GIWP || p.hasPermission("cctv.view.zoom") || p.hasPermission("cctv.view.nightvision") || p.hasPermission("cctv.view.spot"))
            inv.setItem(0, CCTVMenu.getItem(Heads.OPTIONS,lang.CAMERA_VIEW_OPTION));
        if (GIWP || p.hasPermission("cctv.view.move")) {
            inv.setItem(3, Heads.ROTATE_LEFT.get());
            inv.setItem(computer != null && computer.getCameras().size() > 1 ? 4 : 5, Heads.ROTATE_RIGHT.get());
        }
        if ((GIWP || p.hasPermission("cctv.view.switch")) && computer != null && computer.getCameras().size() > 1) {
            inv.setItem(6, Heads.CAM_PREVIOUS.get());
            inv.setItem(7, Heads.CAM_NEXT.get());
        }
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
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_LEFT)) cm.rotateHorizontally(p,get(p).getCamera(), -18);
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_RIGHT)) cm.rotateHorizontally(p,get(p).getCamera(), 18);
        if (itemName.equals(lang.CAMERA_VIEW_PREVIOUS)) switchCamera(p,true);
        if (itemName.equals(lang.CAMERA_VIEW_NEXT)) switchCamera(p,false);
    }

    public void switchCamera(Player p, boolean previous) {
        if (!p.hasPermission("cctv.view.switch")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(p);
        Computer computer = viewer.getComputer();
        if (computer == null) {
            p.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
            return;
        }
        if (computer.getCameras().size() <= 1) {
            p.sendMessage(lang.NO_CAMERAS);
            return;
        }

        List<Camera> cams = new ArrayList<>(computer.getCameras());
        if (previous) Collections.reverse(cams);

        Camera currentCam = viewer.getCamera();
        Camera cam = cams.indexOf(currentCam) == cams.size()-1
                ? cams.get(0)
                : cams.get(cams.indexOf(currentCam)+1);
        cm.viewCameraInstant(cam, p);
        viewer.setCamera(cam);
    }

}
