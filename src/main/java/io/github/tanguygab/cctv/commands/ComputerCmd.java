package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.managers.ComputerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ComputerCmd extends Command {

    private final ComputerManager cpm = cctv.getComputers();

    public ComputerCmd() {
        super("computer");
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("You have to be a player to do this!");
            return;
        }
        String arg = args.length > 1 ? args[1] : "";

        switch (arg) {
            case "get" -> {}
            case "list" -> {}
            case "open" -> {}
            case "teleport" -> {}
            case "setowner" -> {}
            default -> sender.spigot().sendMessage(helpPage("Computer commands",
                    "get:Get the computer item",
                    "list:Get the list of all computers",
                    "open <computer>:Open the computer's menu",
                    "teleport <computer>:Teleport to the computer",
                    "setowner <computer> <player>:Set the computer's owner"));
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

}
