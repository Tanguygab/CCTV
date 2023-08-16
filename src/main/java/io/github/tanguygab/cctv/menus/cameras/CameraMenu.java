package io.github.tanguygab.cctv.menus.cameras;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CameraMenu extends CCTVMenu {

    private final Camera camera;

    public CameraMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
        inv = Bukkit.getServer().createInventory(null, 45, lang.getGuiCamera(camera.getName()));
    }

    @Override
    public void open() {
        fillSlots(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,41,42,43);
        inv.setItem(15,getItem(cctv.getCustomHeads().get(camera.getSkin()),lang.GUI_CAMERA_CHANGE_SKIN));
        updateVisibilityItem();
        updateEnabledItem();
        inv.setItem(33,getItem(Material.NAME_TAG,lang.GUI_CAMERA_RENAME));
        inv.setItem(24,getItem(Material.ITEM_FRAME,lang.GUI_CAMERA_VIEW));

        inv.setItem(11,Heads.ROTATE_UP.get());
        inv.setItem(19,Heads.ROTATE_LEFT.get());
        inv.setItem(21,Heads.ROTATE_RIGHT.get());
        inv.setItem(29,Heads.ROTATE_DOWN.get());

        inv.setItem(40, getItem(Heads.EXIT,lang.GUI_CAMERA_EXIT));
        inv.setItem(44, getItem(Material.BARRIER,lang.GUI_CAMERA_DELETE));
        player.openInventory(inv);
    }

    private void updateVisibilityItem() {
        inv.setItem(23,camera.isShown()
                ? getItem(Material.ENDER_EYE,lang.GUI_CAMERA_SHOWN)
                : getItem(Material.ENDER_PEARL,lang.GUI_CAMERA_HIDDEN));
    }
    private void updateEnabledItem() {
        inv.setItem(25,camera.isEnabled()
                ? getItem(Material.REDSTONE_TORCH,lang.GUI_CAMERA_ENABLED)
                : getItem(Material.LEVER,lang.GUI_CAMERA_DISABLED));
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        CameraManager cm = cctv.getCameras();
        switch (slot) {
            case 15 -> open(new CameraSkinMenu(player,camera));
            case 23 -> {
                camera.setShown(!camera.isShown());
                updateVisibilityItem();
            }
            case 24 -> {
                player.closeInventory();
                cm.viewCamera(player,camera,null,null);
            }
            case 25 -> {
                camera.setEnabled(!camera.isEnabled());
                updateEnabledItem();
            }
            case 33 -> {
                Listener.cameraRename.put(player,camera);
                player.closeInventory();
                player.sendMessage(lang.CHAT_PROVIDE_NAME,lang.CHAT_TYPE_CANCEL);
            }
            case 11 -> cm.rotate(player,camera, -9,false);
            case 19 -> cm.rotate(player,camera, 18,true);
            case 21 -> cm.rotate(player,camera, -18,true);
            case 29 -> cm.rotate(player,camera, 9,false);

            case 40 -> back();
            case 44 -> {
                player.closeInventory();
                cm.delete(camera.getName());
                Utils.giveOrDrop(player,cctv.getCustomHeads().get(camera.getSkin()));
                player.sendMessage(lang.getCameraDeleted(camera.getName()));
            }
        }
    }
}
