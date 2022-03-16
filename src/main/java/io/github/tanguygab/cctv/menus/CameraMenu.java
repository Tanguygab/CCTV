package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CameraMenu extends CCTVMenu {

    private final Camera camera;

    public CameraMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.getGuiCameraDelete(camera.getId()));
        inv.setItem(0,Utils.getItem(cctv.getCustomHeads().get(camera.getSkin()), "&7Change Camera Skin"));
        inv.setItem(2, Utils.getItem(Material.BARRIER,lang.GUI_CAMERA_DELETE_ITEM_DELETE));
        inv.setItem(4, Utils.getItem(Heads.EXIT,lang.GUI_CAMERA_DELETE_ITEM_CANCEL));
        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot) {
        switch (slot) {
            case 0 -> {
                p.sendMessage("WIP!");
            }
            case 2 -> {
                p.closeInventory();
                cctv.getCameras().delete(camera.getId(), p);
            }
            case 4 -> p.closeInventory();
        }
    }
}
