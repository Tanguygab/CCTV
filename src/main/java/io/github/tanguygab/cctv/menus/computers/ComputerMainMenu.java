package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CameraMenu;
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

import java.text.DecimalFormat;
import java.util.List;

public class ComputerMainMenu extends ComputerMenu {

    private static final DecimalFormat f = new DecimalFormat("#.##");

    public ComputerMainMenu(Player p, Computer computer) {
        super(p,computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerDefault(page+""));

        fillSlots(9,18);
        inv.setItem(0, getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));

        CameraGroup group = computer.getCameraGroup();
        if (group != null)
            list(group.getCameras(),cam->{
                ItemStack item = getItem(cctv.getCustomHeads().get(cam.getSkin()), "&eCamera: " + cam.getId());
                Location loc = cam.getLocation();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(List.of("",ChatColor.translateAlternateColorCodes('&',
                                "&6X: &7"+f.format(loc.getX())
                                        +" &6Y: &7"+f.format(loc.getY())
                                        +" &6Z: &7"+f.format(loc.getZ())
                        ),""
                        ,ChatColor.YELLOW+"Left-Click to View"
                        ,ChatColor.YELLOW+"Right-Click to Edit"
                ));
                item.setItemMeta(meta);
                inv.addItem(item);
            });

        p.openInventory(inv);
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
                if (click.isRightClick()) {
                    if (!camera.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.camera.other"))
                        p.sendMessage(lang.NO_PERMISSIONS);
                    else open(new CameraMenu(p,camera));
                    return;
                }
                else cctv.getCameras().viewCamera(p, camera, computer.getCameraGroup());
                p.closeInventory();
            }
        }
    }
}
