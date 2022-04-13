package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerOptionsMenu extends ComputerMenu {

    public ComputerOptionsMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.GUI_COMPUTER_OPTIONS_ITEM);

        inv.setItem(0, Heads.COMPUTER_BACK.get());
        setPublicItem();
        setGroupItem();

        p.openInventory(inv);
    }

    private void setPublicItem() {
        ItemStack item = getItem(computer.isPublic() ? Material.CHEST : Material.ENDER_CHEST,"&aComputer Access");
        ItemMeta meta = item.getItemMeta();
        meta.setLore(List.of("",
                ChatColor.GOLD+"Status: "+ChatColor.YELLOW+(computer.isPublic() ? "Public" : "Private"),
                "",
                ChatColor.YELLOW+"Left-Click to toggle",
                ChatColor.YELLOW+"Middle-Click to add players",
                ChatColor.YELLOW+"Right-Click to remove players")
        );
        item.setItemMeta(meta);
        inv.setItem(3, item);
    }

    private void setGroupItem() {
        ItemStack item = Heads.COMPUTER_SET_CAMGROUP.get();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(List.of("",
                ChatColor.GOLD+"Current Group: "+ChatColor.YELLOW+(computer.getCameraGroup() == null ? "None" : computer.getCameraGroup().getId()),
                "",
                ChatColor.YELLOW+"Click to set a group",
                ChatColor.YELLOW+"Drop to remove current")
        );
        item.setItemMeta(meta);
        inv.setItem(4, item);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> open(new ComputerMainMenu(p,computer));
            case 3 -> {
                switch (click) {
                    case LEFT,SHIFT_LEFT -> {
                        computer.setPublic(!computer.isPublic());
                        setPublicItem();
                    }
                    case MIDDLE -> open(new ComputerAddPlayersMenu(p,computer));
                    case RIGHT,SHIFT_RIGHT -> open(new ComputerRemovePlayerMenu(p,computer));
                }
            }
            case 4 -> {
                if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
                    computer.setCameraGroup(null);
                    setGroupItem();
                    return;
                }
                open(new ComputerSetGroupMenu(p,computer));
            }
        }
    }
}
