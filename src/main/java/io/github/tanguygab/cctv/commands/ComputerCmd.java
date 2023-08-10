package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.utils.Utils;
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
                if (noPerm(p, "create")) {
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
            case "list" -> {
                if (noPerm(p, "list")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                int page = 1;
                if (args.length > 2) {
                    try {page = Integer.parseInt(args[2]);}
                    catch (Exception ignored) {}
                }
                p.spigot().sendMessage(list("Computers",cpm.get(p),"open","Click to open!",page));
            }
            case "open" -> {
                if (noPerm(p, "open")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                cpm.open(p,computer);
            }
            case "teleport" -> {
                if (noPerm(p, "teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                Location loc = computer.getLocation().clone();
                loc.add(1.0D,0.5D,0.5D);
                p.teleport(loc);
            }
            case "setowner" -> {
                if (noPerm(p, "setowner")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
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
            case "info" -> {
                if (noPerm(p, "info")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                OfflinePlayer off = Bukkit.getServer().getOfflinePlayer(UUID.fromString(computer.getOwner()));
                String owner = off.getName() == null ? "Unknown" : off.getName();
                TextComponent comp = comp("Computer Info:",ChatColor.GOLD);
                comp.setBold(true);
                comp.addExtra(comp("\nName: ", computer.getId()));
                comp.addExtra(comp("\nOwner: ", owner));
                comp.addExtra(comp("\nCameras:",ChatColor.GOLD));

                for (Camera cam : computer.getCameras()) {
                    TextComponent camComp = comp("\n - "+cam.getId(),ChatColor.YELLOW);
                    camComp.setBold(false);
                    comp.addExtra(camComp);
                }
                p.spigot().sendMessage(comp);
            }
            case "addcamera" -> {
                if (noPerm(p, "addcamera")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                String cam = args[3];
                if (!cctv.getCameras().exists(cam)) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                Camera camera = cctv.getCameras().get(cam);
                if (computer.getCameras().contains(camera)) {
                    p.sendMessage(lang.COMPUTER_CAMERA_ALREADY_ADDED);
                    return;
                }
                computer.addCamera(camera);
                p.sendMessage(lang.COMPUTER_CAMERA_ADDED);
            }
            case "removecamera" -> {
                if (noPerm(p, "removecamera")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                String cam = args[3];
                if (!cctv.getCameras().exists(cam)) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                Camera camera = cctv.getCameras().get(cam);
                if (!computer.getCameras().contains(camera)) {
                    p.sendMessage(lang.COMPUTER_CAMERA_NOT_FOUND);
                    return;
                }
                computer.removeCamera(camera);
                p.sendMessage(lang.COMPUTER_CAMERA_REMOVED);
            }
            case "public" -> {
                if (noPerm(p, "setowner")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a computer name!");
                    return;
                }
                Computer computer = cpm.get(args[2]);
                if (computer == null || cantUse(p, computer.getOwner())) {
                    p.sendMessage(lang.COMPUTER_NOT_FOUND);
                    return;
                }
                computer.setPublic(!computer.isPublic());
                if (computer.isPublic())
                    p.sendMessage(ChatColor.GREEN + "Computer now public!");
                else p.sendMessage(ChatColor.RED + "Computer now private!");
            }
            default -> sender.spigot().sendMessage(helpPage("Computer commands",
                    "get:Get the computer item",
                    "list:Get the list of all computers",
                    "open <computer>:Open the computer's menu",
                    "teleport <computer>:Teleport to the computer",
                    "setowner <computer> <player>:Set the computer's owner",
                    "info <group>:Get the computer's info",
                    "addcamera <group> <camera>:Add a camera to your computer",
                    "removecamera <group> <camera>:Remove a camera from your computer",
                    "public <computer>: Toggle public access of the computer"));
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("get","list","open","teleport","setowner","info","addcamera","removecamera","public");
            case 3 -> switch (args[1].toLowerCase()) {
                case "get","list" -> null;
                default -> sender instanceof Player p ? cpm.get(p) : Utils.list(cpm.values());
            };
            case 4 -> switch (args[1].toLowerCase()) {
                case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                case "addcamera" -> {
                    CameraManager cm = cctv.getCameras();
                    List<String> addedCameras = cpm.exists(args[2]) ? Utils.list(cpm.get(args[2]).getCameras()) : List.of();
                    List<String> list = sender instanceof Player p ? cm.get(p) : Utils.list(cm.values());
                    list.removeAll(addedCameras);
                    yield list;
                }
                case "removecamera" -> cpm.exists(args[2]) ? Utils.list(cpm.get(args[2]).getCameras()) : List.of();
                default -> null;
            };
            default -> null;
        };
    }

}
