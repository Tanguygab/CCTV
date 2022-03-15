package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GroupCmd extends Command {

    private final CameraGroupManager cgm = cctv.getCameraGroups();
    private final CameraManager cm = cctv.getCameras();

    public GroupCmd() {
        super("group");
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("You have to be a player to do this!");
            return;
        }
        String arg = args.length > 1 ? args[1] : "";

        switch (arg) {
            case "create" -> {
                if (!hasPerm(p,"create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) cgm.create(args[2],p);
                else p.sendMessage(ChatColor.RED + "Please specify a group name!");
            }
            case "delete" -> {
                if (!hasPerm(p,"delete")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                p.sendMessage(lang.GROUP_DELETE);
                cgm.delete(group.getId());
            }
            case "list" -> {
                if (!hasPerm(p,"list")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                p.spigot().sendMessage(list("Camera Groups",cgm.get(p),"info","Click to get its info!"));
            }
            case "info" -> {
                if (!hasPerm(p,"info")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                OfflinePlayer off = Bukkit.getServer().getOfflinePlayer(UUID.fromString(group.getOwner()));
                String owner = off.getName() == null ? "Unknown" : off.getName();
                TextComponent comp = comp("Camera Group info:",ChatColor.GOLD);
                comp.setBold(true);
                comp.addExtra(comp("\nName: ",ChatColor.GOLD,group.getId(),ChatColor.YELLOW));
                comp.addExtra(comp("\nOwner: ",ChatColor.GOLD,owner,ChatColor.YELLOW));
                comp.addExtra(comp("\nCameras:",ChatColor.GOLD));

                for (Camera cam : group.getCameras()) {
                    TextComponent camComp = comp("\n - "+cam.getId(),ChatColor.YELLOW);
                    camComp.setBold(false);
                    comp.addExtra(camComp);
                }
                p.spigot().sendMessage(comp);
            }
            case "addcamera" -> {
                if (!hasPerm(p,"addcamera")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                String cam = args[3];
                if (!cm.exists(cam)) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                Camera camera = cm.get(cam);
                if (group.getCameras().contains(camera)) {
                    p.sendMessage(lang.GROUP_CAMERA_ALREADY_ADDED);
                    return;
                }
                group.getCameras().add(camera);
                p.sendMessage(lang.GROUP_CAMERA_ADDED);
            }
            case "removecamera" -> {
                if (!hasPerm(p,"removecamera")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                String cam = args[3];
                if (!cm.exists(cam)) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                Camera camera = cm.get(cam);
                if (!group.getCameras().contains(camera)) {
                    p.sendMessage(lang.GROUP_DOES_NOT_CONTAIN_CAMERA);
                    return;
                }
                group.getCameras().remove(camera);
                p.sendMessage(lang.GROUP_REMOVE_CAMERA);
            }
            case "rename" -> {
                if (!hasPerm(p,"rename")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a new name!");
                    return;
                }
                String newName = args[3];
                if (cgm.exists(newName)) {
                    p.sendMessage(lang.GROUP_ALREADY_EXISTS);
                    return;
                }
                group.setId(newName);
                p.sendMessage(lang.getGroupRenamed(newName));
            }
            case "setowner" -> {
                if (!hasPerm(p,"setowner")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                CameraGroup group = cgm.get(args[2]);
                if (group == null || !canUse(p,group.getOwner())) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
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
                if (group.getOwner().equals(uuid)) {
                    p.sendMessage(lang.GROUP_PLAYER_ALREADY_OWNER);
                    return;
                }
                group.setOwner(uuid);
                p.sendMessage(lang.getGroupOwnerChanged(newOwner.getName()));
            }
            default -> sender.spigot().sendMessage(helpPage("Camera Group commands",
                    "create <name>:Create a new group",
                    "delete <name>:Delete a group",
                    "list:Get the list of all groups",
                    "info <group>:Get the group's info",
                    "addcamera <group> <camera>:Add a camera to your group",
                    "removecamera <group> <camera>:Remove a camera from your group",
                    "rename <group> <name>:Rename the group",
                    "setowner <group> <player>:Change the group's owner"));
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("create","delete","addcamera","removecamera","setowner","rename","info","list");
            case 3 -> switch (args[1].toLowerCase()) {
                case "addcamera","removecamera", "info" -> sender instanceof Player p ? cgm.get(p) : Utils.list(cgm.values());
                default -> null;
            };
            case 4 -> {
                String group = args[2];
                List<String> addedCameras = cgm.exists(group) ? Utils.list(cgm.get(group).getCameras()) : List.of();
                yield switch (args[1].toLowerCase()) {
                    case "addcamera" -> {
                        List<String> list = sender instanceof Player p ? cm.get(p) : Utils.list(cm.values());
                        list.removeAll(addedCameras);
                        yield list;
                    }
                    case "removecamera" -> addedCameras;
                    case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                    default -> null;
                };
            }
            default -> null;
        };
    }

}
