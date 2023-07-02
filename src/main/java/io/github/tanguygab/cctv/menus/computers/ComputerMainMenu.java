package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.cameras.CameraMenu;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerMainMenu extends ComputerMenu {

    public ComputerMainMenu(Player p, Computer computer) {
        super(p,computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerDefault(String.valueOf(page)));

        fillSlots(9,18);
        inv.setItem(0, getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));

        if (computer.isAdmin()) {
            list(cctv.getCameras().values(),this::loadCamera);
            p.openInventory(inv);
            return;
        }
        CameraGroup group = computer.getCameraGroup();
        if (group != null) list(group.getCameras(),this::loadCamera);

        p.openInventory(inv);
    }

    private void loadCamera(Camera cam) {
        ItemStack item = getItem(cctv.getCustomHeads().get(cam.getSkin()), "&eCamera: " + cam.getId());
        Location loc = cam.getLocation();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(List.of("",ChatColor.translateAlternateColorCodes('&',
                        "&6X: &7"+ posFormat.format(loc.getX())
                                +" &6Y: &7"+ posFormat.format(loc.getY())
                                +" &6Z: &7"+ posFormat.format(loc.getZ())
                ),""
                ,ChatColor.YELLOW+"Left-Click to View"
                ,ChatColor.YELLOW+"Right-Click to Edit"
                ,""
                ,ChatColor.YELLOW+"Shift-Left to go up"
                ,ChatColor.YELLOW+"Shift-Right to go down"
                ,ChatColor.RED+"Drop to remove"
        ));
        item.setItemMeta(meta);
        inv.addItem(item);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> {
                if (computer.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.computer.other"))
                    open(new ComputerOptionsMenu(p,computer));
                else p.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
            }
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> p.closeInventory();
            default -> {
                if (item == null || item.getType() == Material.AIR) return;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String itemName = ChatColor.stripColor(meta.getDisplayName());
                if (!itemName.startsWith("Camera: ")) return;
                String cam = itemName.substring(8);
                Camera camera = cctv.getCameras().get(cam);
                if (camera == null) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                switch (click) {
                    case LEFT -> {
                        cctv.getCameras().viewCamera(p, camera, computer.getCameraGroup());
                        p.closeInventory();
                    }
                    case RIGHT -> {
                        if (!camera.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.camera.other")) {
                            p.sendMessage(lang.NO_PERMISSIONS);
                        }
                        else open(new CameraMenu(p,camera));
                    }
                    case SHIFT_LEFT, SHIFT_RIGHT -> {
                        CameraGroup g = computer.getCameraGroup();
                        int bound = click == ClickType.SHIFT_LEFT ? 0 : g.getCameras().size()-1;
                        int inc = click == ClickType.SHIFT_LEFT ? -1 : 1;

                        int index = g.getCameras().indexOf(camera);
                        if (index == bound) return;
                        g.removeCamera(camera);
                        g.getCameras().add(index+inc,camera);
                        open();
                    }
                    case DROP -> {
                        computer.getCameraGroup().removeCamera(camera);
                        p.sendMessage(lang.GROUP_REMOVE_CAMERA);
                        open();
                    }
                }
            }
        }
    }
}
