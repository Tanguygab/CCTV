package io.github.tanguygab.cctv.menus.cameras;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.Heads;
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
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 45, lang.getGuiCamera(camera.getName()));

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
        p.openInventory(inv);
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
            case 15 -> open(new CameraSkinMenu(p,camera));
            case 23 -> {
                camera.setShown(!camera.isShown());
                updateVisibilityItem();
            }
            case 24 -> {
                p.closeInventory();
                cm.viewCamera(p,camera,null,null);
            }
            case 25 -> {
                camera.setEnabled(!camera.isEnabled());
                updateEnabledItem();
            }
            case 33 -> {
                Listener.cameraRename.put(p,camera);
                p.closeInventory();
                p.sendMessage(lang.CHAT_PROVIDE_NAME+"\n"+lang.CHAT_TYPE_CANCEL);
            }
            case 11 -> cm.rotate(p,camera, -9,false);
            case 19 -> cm.rotate(p,camera, 18,true);
            case 21 -> cm.rotate(p,camera, -18,true);
            case 29 -> cm.rotate(p,camera, 9,false);

            case 40 -> back();
            case 44 -> {
                p.closeInventory();
                cm.delete(camera.getName(), p);
            }
        }
    }
}
