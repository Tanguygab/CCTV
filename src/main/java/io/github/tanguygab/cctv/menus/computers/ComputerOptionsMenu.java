package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ComputerOptionsMenu extends ComputerMenu {

    public ComputerOptionsMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.GUI_COMPUTER_OPTIONS_ITEM);

        inv.setItem(0, Heads.COMPUTER_BACK.get());
        inv.setItem(2, Heads.COMPUTER_SET_CAMGROUP.get());
        inv.setItem(3, Heads.COMPUTER_ADD_PLAYER.get());
        inv.setItem(4, Heads.COMPUTER_REMOVE_PLAYER.get());

        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> open(new ComputerMainMenu(p,computer));
            case 2 -> open(new ComputerSetGroupMenu(p,computer));
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
