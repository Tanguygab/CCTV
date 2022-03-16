package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ComputerMenu extends CCTVMenu {

    protected final Computer computer;
    protected int page = 1;

    protected ComputerMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    public void open() {}

    protected void setPage(int page) {}

    @Override
    public void onClick(ItemStack item, int slot) {}
}
