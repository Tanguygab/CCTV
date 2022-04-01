package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.ViewerOptionsMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ViewerManager extends Manager<Viewer> {

    public boolean CISWP;
    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;

    private final CameraManager cm = cctv.getCameras();

    public ViewerManager() {
        super();
    }

    @Override
    public void load() {
        ConfigurationFile config = cctv.getConfiguration();
        CISWP = config.getBoolean("camera_inventory_show_item_without_permissions");
        TIME_TO_CONNECT = config.getInt("time_to_connect",3);
        TIME_TO_DISCONNECT = config.getInt("time_to_disconnect",3);
        TIME_FOR_SPOT = config.getInt("time_for_spot",5);
    }

    @Override
    public void delete(String id, Player player) {
        Viewer viewer = get(id);
        if (viewer == null) {
            if (player != null) player.sendMessage("This player isn't viewing a camera!");
            return;
        }
        Player p = get(viewer);
        p.getInventory().setContents(viewer.getInv());

        p.removePotionEffect(PotionEffectType.SLOW);
        p.setCanPickupItems(true);
        delete(id);
    }

    public void delete(Player p) {delete(p.getUniqueId().toString(),null);}
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }
    public Player get(Viewer viewer) {
        return Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
    }

    public boolean exists(Player p) {
        return exists(p.getUniqueId().toString());
    }

    public void createPlayer(Player p, Camera cam, CameraGroup group) {
        Viewer viewer = new Viewer(p,cam,group);
        map.put(viewer.getId(),viewer);

        p.setCanPickupItems(false);
        giveViewerItems(p,group);

        for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(cctv,p);
    }

    private void giveViewerItems(Player p, CameraGroup group) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (CISWP || p.hasPermission("cctv.view.zoom") || p.hasPermission("cctv.view.nightvision") || p.hasPermission("cctv.view.spot"))
            inv.setItem(0, CCTVMenu.getItem(Heads.OPTIONS,lang.CAMERA_VIEW_OPTION));
        if (CISWP || p.hasPermission("cctv.view.move")) {
            inv.setItem(3, Heads.ROTATE_LEFT.get());
            inv.setItem(group != null && group.getCameras().size() > 1 ? 4 : 5, Heads.ROTATE_RIGHT.get());
        }
        if ((CISWP || p.hasPermission("cctv.view.switch")) && group != null && group.getCameras().size() > 1) {
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> cm.unviewCamera(p),  TIME_TO_DISCONNECT * 20L);
            return;
        }
        if (itemName.equals(lang.CAMERA_VIEW_OPTION)) cctv.openMenu(p,new ViewerOptionsMenu(p));
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_LEFT)) cm.rotateHorizontally(p,get(p).getCamera(), -18);
        if (itemName.equals(lang.CAMERA_VIEW_ROTATE_RIGHT)) cm.rotateHorizontally(p,get(p).getCamera(), 18);
        if (itemName.equals(lang.CAMERA_VIEW_PREVIOUS)) switchCamera(p,true);
        if (itemName.equals(lang.CAMERA_VIEW_NEXT)) switchCamera(p,false);
    }

    private void switchCamera(final Player p, boolean previous) {
        if (!p.hasPermission("cctv.view.switch")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(p);
        CameraGroup group = viewer.getGroup();
        if (group == null) {
            p.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
            return;
        }
        if (group.getCameras().size() <= 1) {
            p.sendMessage(lang.NO_CAMERAS);
            return;
        }

        List<Camera> cams = new ArrayList<>(group.getCameras());
        if (previous) Collections.reverse(cams);

        Camera currentCam = viewer.getCamera();
        Camera cam = cams.indexOf(currentCam) == cams.size()-1
                ? cams.get(0)
                : cams.get(cams.indexOf(currentCam)+1);
        cm.viewCameraInstant(cam, p);
        viewer.setCamera(cam);
    }


}
