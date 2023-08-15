package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ComputerCmd extends Command<Computer> {

    private final ComputerManager cpm = cctv.getComputers();
    @Getter(AccessLevel.PROTECTED) private final String notFound = lang.COMPUTER_NOT_FOUND;

    public ComputerCmd() {
        super("computer");
    }

    @Override
    protected Computer get(String name) {
        return cpm.get(name);
    }

    @Override
    protected String getOwner(Computer computer) {
        return computer.getOwner();
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("You have to be a player to do this!");
            return;
        }
        String arg = args.length > 1 ? args[1] : "";

        switch (arg) {
            case "get" -> {
                if (noPerm(p, "get")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                p.getInventory().addItem((args.length > 2
                        && args[2].equalsIgnoreCase("admin")
                        && p.hasPermission("cctv.admin.computer")
                        && cpm.ADMIN_COMPUTER_ITEM != null
                        ? cpm.ADMIN_COMPUTER_ITEM
                        : cpm.COMPUTER_ITEM
                ).clone());
                p.sendMessage(ChatColor.GREEN + "Place down this item to create a computer!");
            }
            case "list" -> list(p,"Computers",cpm.get(p), "Click to open!",args);
            case "teleport" -> {
                if (noPerm(p, "teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                Computer computer = checkExist(p,args);
                if (computer == null) return;
                Location loc = computer.getLocation().clone();
                loc.add(1.0D,0.5D,0.5D);
                p.teleport(loc);
            }
            case "setowner" -> {
                Computer computer = checkExist(p,args);
                if (computer == null) return;
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
            case "info" -> {
                Computer computer = checkExist(p,args);
                if (computer == null) return;
                OfflinePlayer off = Bukkit.getServer().getOfflinePlayer(UUID.fromString(computer.getOwner()));
                String owner = off.getName() == null ? "Unknown" : off.getName();
                TextComponent comp = comp("Computer Info:",ChatColor.GOLD);
                comp.setBold(true);
                comp.addExtra(comp("\nName: ", computer.getName()));
                comp.addExtra(comp("\nOwner: ", owner));
                comp.addExtra(comp("\nCameras:",ChatColor.GOLD));

                for (Computable cam : computer.getCameras()) {
                    TextComponent camComp = comp("\n - "+cam.getName(),ChatColor.YELLOW);
                    camComp.setBold(cam instanceof CameraGroup);
                    comp.addExtra(camComp);
                }
                p.spigot().sendMessage(comp);
            }
            default -> helpPage(p,"Computer commands",
                    "get:Get the computer item",
                    "list:Get the list of all computers",
                    "open <computer>:Open the computer's menu",
                    "teleport <computer>:Teleport to the computer",
                    "setowner <computer> <player>:Set the computer's owner",
                    "info <group>:Get the computer's info");
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("get","list","teleport","setowner","info");
            case 3 -> switch (args[1].toLowerCase()) {
                case "get","list" -> null;
                default -> sender instanceof Player p ? cpm.get(p) : cpm.values().stream().map(Computer::getName).toList();
            };
            case 4 -> args[1].equalsIgnoreCase("setowner") ? Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList() : null;
            default -> null;
        };
    }

}
