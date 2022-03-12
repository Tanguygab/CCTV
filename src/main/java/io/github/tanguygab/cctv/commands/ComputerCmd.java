package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ComputerCmd extends Command<Computer> {

    public ComputerCmd() {
        super("computer");
    }

    public void onCommand(CommandSender sender, String[] args) {

    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

}
