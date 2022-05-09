package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ComputerMenu extends CCTVMenu {

    protected final Computer computer;

    protected ComputerMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    public void open() {}

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {}
}
