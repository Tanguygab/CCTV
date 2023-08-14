package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerAddCamerasMenu extends ComputerMenu {

    protected ComputerAddCamerasMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerAddCamera(page));

        fillSlots(0,9,18);
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

        list(cctv.getCameras().get(p).stream().filter(cam->!computer.getCameras().contains(cctv.getCameras().get(cam))).toList(),camera->{
            Camera cam = cctv.getCameras().get(camera);
            ItemStack item = getItem(cctv.getCustomHeads().get(cam.getSkin()), lang.GUI_COMPUTER_CAMERA_ITEM_NAME + cam.getName());
            Location loc = cam.getLocation();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(List.of("",ChatColor.translateAlternateColorCodes('&',
                    lang.GUI_COMPUTER_CAMERA_ITEM_X+ posFormat.format(loc.getX())
                                    +lang.GUI_COMPUTER_CAMERA_ITEM_Y+ posFormat.format(loc.getY())
                                    +lang.GUI_COMPUTER_CAMERA_ITEM_Z+ posFormat.format(loc.getZ())
            )));
            item.setItemMeta(meta);
            inv.addItem(item);
        });

        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> back();
            default -> {
                String camera = getKey(item,cctv.getCameras().cameraKey);
                if (camera == null) return;
                Camera cam = cctv.getCameras().get(camera);
                computer.addCamera(cam);
                setPage(page);
                p.sendMessage(lang.COMPUTER_CAMERA_ADDED);
            }
        }
    }
}
