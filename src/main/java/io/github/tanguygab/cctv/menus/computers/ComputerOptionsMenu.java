package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        setGroupItem();
        inv.setItem(3, Heads.COMPUTER_ADD_PLAYER.get());
        inv.setItem(4, Heads.COMPUTER_REMOVE_PLAYER.get());

        p.openInventory(inv);
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
        inv.setItem(2, item);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> open(new ComputerMainMenu(p,computer));
            case 2 -> {
                if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
                    computer.setCameraGroup(null);
                    setGroupItem();
                    return;
                }
                open(new ComputerSetGroupMenu(p,computer));
            }
            case 3 -> {
                Listener.chatInput.add(p);
                p.closeInventory();
                p.sendMessage(lang.CHAT_PROVIDE_PLAYER);
                p.sendMessage(lang.CHAT_TYPE_EXIT);
            }
            case 4 -> open(new ComputerRemovePlayerMenu(p,computer));
        }
    }
}
