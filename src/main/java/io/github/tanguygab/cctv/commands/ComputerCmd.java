package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
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
            case "get" -> {
                if (!hasPerm(p,"create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                p.getInventory().addItem(Utils.getComputer());
            }
            case "list" -> {
                if (!hasPerm(p,"list")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                p.spigot().sendMessage(list("Computers",cpm.get(p),"open","Click to open!"));
            }
            case "open" -> {
                if (!hasPerm(p,"open")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || !canUse(p,computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                cpm.open(p,computer);
            }
            case "teleport" -> {
                if (!hasPerm(p,"teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || !canUse(p,computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                cpm.teleport(p,computer);
            }
            case "setowner" -> {
                if (!hasPerm(p,"setowner")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || !canUse(p,computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a new owner!");
                    return;
                }
                OfflinePlayer newOwner = Utils.getOfflinePlayer(args[3]);
                if (newOwner == null) {
                    p.sendMessage(lang.PLAYER_NOT_FOUND);
                    return;
                }
                String uuid = newOwner.getUniqueId().toString();
                if (computer.getOwner().equals(uuid)) {
                    p.sendMessage(lang.COMPUTER_PLAYER_ALREADY_OWNER);
                    return;
                }
                computer.setOwner(uuid);
                p.sendMessage(lang.getComputerOwnerChanged(newOwner.getName()));
            }
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
