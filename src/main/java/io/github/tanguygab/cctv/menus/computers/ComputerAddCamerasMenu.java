package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
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
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerAddCamera(page+""));

        fillSlots(0,9,18);
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

        CameraGroup group = computer.getCameraGroup();
        if (group != null)
            list(cctv.getCameras().get(p).stream().filter(cam->!group.getCameras().contains(cctv.getCameras().get(cam))).toList(),camera->{
                Camera cam = cctv.getCameras().get(camera);
                ItemStack item = getItem(cctv.getCustomHeads().get(cam.getSkin()), "&eCamera: " + cam.getId());
                Location loc = cam.getLocation();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(List.of("",ChatColor.translateAlternateColorCodes('&',
                                "&6X: &7"+ posFormat.format(loc.getX())
                                        +" &6Y: &7"+ posFormat.format(loc.getY())
                                        +" &6Z: &7"+ posFormat.format(loc.getZ())
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
                if (item == null || item.getType() == Material.AIR) return;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String itemName = ChatColor.stripColor(meta.getDisplayName());
                if (!itemName.startsWith("Camera: ")) return;
                String camera = itemName.substring(8);
                Camera cam = cctv.getCameras().get(camera);
                computer.getCameraGroup().addCamera(cam);
                setPage(page);
                p.sendMessage(lang.GROUP_CAMERA_ADDED);
            }
        }
    }
}
