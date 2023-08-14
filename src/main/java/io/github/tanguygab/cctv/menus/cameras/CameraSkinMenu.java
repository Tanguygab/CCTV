package io.github.tanguygab.cctv.menus.cameras;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.CustomHeads;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class CameraSkinMenu extends CCTVMenu {

    private final Camera camera;
    private final CustomHeads heads;

    public CameraSkinMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
        heads = cctv.getCustomHeads();
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiCameraSkin(page));

        fillSlots(9,18);
        updateCameraItem();
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, getItem(Heads.EXIT,lang.GUI_CAMERA_EXIT));

        List<String> list = heads.getHeads();
        list.add(lang.GUI_CAMERA_SKIN_DEFAULT);
        list(list,skin-> inv.addItem(getItem(heads.get(skin),skin)));
        p.openInventory(inv);
    }

    private void updateCameraItem() {
        inv.setItem(0, getItem(heads.get(camera.getSkin()),lang.GUI_CAMERA_SKIN_CURRENT +camera.getSkin()));
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> back();
            default -> {
                String skin = getKey(item,cctv.getCameras().cameraKey);
                if (skin == null) return;
                camera.setSkin(lang.GUI_CAMERA_SKIN_DEFAULT);
                updateCameraItem();
            }
        }
    }
}
