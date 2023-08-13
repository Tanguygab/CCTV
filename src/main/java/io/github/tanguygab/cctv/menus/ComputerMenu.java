package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.entity.Player;

public abstract class ComputerMenu extends CCTVMenu {

    protected final Computer computer;

    protected ComputerMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }
}
