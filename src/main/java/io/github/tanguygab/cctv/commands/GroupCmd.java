package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GroupCmd extends Command<CameraGroup> {

    private final CameraGroupManager cgm = cctv.getGroups();
    private final CameraManager cm = cctv.getCameras();

    public GroupCmd() {
        super("group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("You have to be a player to do this!");
            return;
        }
        String arg = args.length > 1 ? args[1] : "";

        switch (arg) {
            case "create" -> {
                if (noPerm(p, "create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) cgm.create(args[2],p);
                else p.sendMessage(ChatColor.RED + "Please specify a computer name!");
            }
            case "list" -> list(p,"Groups",cgm.get(p), "Click to view!",args);
            case "seticon" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;
                if (args.length < 3) {
                    p.sendMessage("Please specify an icon!");
                    return;
                }
                Material material = Material.getMaterial(args[2]);
                if (!cgm.getAllowedIcons().contains(material)) {
                    p.sendMessage("Invalid icon!");
                    return;
                }
                group.setIcon(material);
                p.sendMessage("Icon changed!");
            }
            case "setowner" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;

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
            case "rename" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;

                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a new name!");
                    return;
                }
                String newName = args[3];
                if (group.rename(newName))
                    p.sendMessage(lang.getGroupRenamed(newName));
                else p.sendMessage(lang.GROUP_ALREADY_EXISTS);
            }
            case "info" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;
                OfflinePlayer off = Bukkit.getServer().getOfflinePlayer(UUID.fromString(group.getOwner()));
                String owner = off.getName() == null ? "Unknown" : off.getName();
                TextComponent comp = comp("Group Info:",ChatColor.GOLD);
                comp.setBold(true);
                comp.addExtra(comp("\nName: ", group.getName()));
                comp.addExtra(comp("\nOwner: ", owner));
                comp.addExtra(comp("\nCameras:",ChatColor.GOLD));

                for (Computable cam : group.getCameras()) {
                    TextComponent camComp = comp("\n - "+cam.getName(),ChatColor.YELLOW);
                    camComp.setBold(cam instanceof CameraGroup);
                    comp.addExtra(camComp);
                }
                p.spigot().sendMessage(comp);
            }
            case "addcamera" -> {
                CameraGroup group = checkExist(p,args);
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
                group.addCamera(camera);
                p.sendMessage(lang.GROUP_CAMERA_ADDED);
            }
            case "removecamera" -> {
                CameraGroup group = checkExist(p,args);
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
                    p.sendMessage(lang.GROUP_CAMERA_NOT_FOUND);
                    return;
                }
                group.removeCamera(camera);
                p.sendMessage(lang.GROUP_CAMERA_REMOVED);
            }
            case "addgroup" -> {
                CameraGroup group = checkExist(p,args);
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                String g = args[3];
                if (!cgm.exists(g)) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                CameraGroup group2 = cgm.get(g);
                if (group.getCameras().contains(group2)) {
                    //p.sendMessage(lang.GROUP_GROUP_ALREADY_ADDED);
                    return;
                }
                group.addCamera(group2);
                //p.sendMessage(lang.GROUP_GROUP_ADDED);
            }
            case "removegroup" -> {
                CameraGroup group = checkExist(p,args);
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a group name!");
                    return;
                }
                String g = args[3];
                if (!cgm.exists(g)) {
                    p.sendMessage(lang.GROUP_NOT_FOUND);
                    return;
                }
                CameraGroup group2 = cgm.get(g);
                if (!group.getCameras().contains(group2)) {
                    //p.sendMessage(lang.GROUP_GROUP_NOT_FOUND);
                    return;
                }
                group.removeCamera(group2);
                //p.sendMessage(lang.GROUP_GROUP_REMOVED);
            }
            default -> helpPage(p,"Group commands",
                    "create <name>:Create a new group",
                    "list:Get the list of all groups",
                    "seticon <group> <icon>:Set the group's icon",
                    "setowner <group> <player>:Set the group's owner",
                    "rename <group> <name>:Rename the group",
                    "info <group>:Get the group's info",
                    "addgroup <group> <group>:Add a group to your group",
                    "removegroup <group> <group>:Remove a group from your group",
                    "addcamera <group> <camera>:Add a camera to your group",
                    "removecamera <group> <camera>:Remove a camera from your group");
        }
    }

    private List<String> getGroups(CommandSender sender, String group, boolean toRemove) {
        if (!cgm.exists(group)) return List.of();
        List<String> added = cgm.get(group).getCameras().stream().filter(c->c instanceof CameraGroup).map(Computable::getName).toList();
        if (toRemove) return added;

        List<String> list = cgm.get((Player) sender);
        list.removeAll(added);
        return list;
    }
    private List<String> getCameras(CommandSender sender, String group, boolean toRemove) {
        if (!cgm.exists(group)) return List.of();
        List<String> added = cgm.get(group).getCameras().stream().filter(c->c instanceof Camera).map(Computable::getName).toList();
        if (toRemove) return added;

        List<String> list = cm.get((Player) sender);
        list.removeAll(added);
        return list;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("create","list","seticon","setowner","rename","info","addgroup","removegroup","addcamera","removecamera");
            case 3 -> switch (args[1].toLowerCase()) {
                case "create","list" -> null;
                default -> cgm.get((Player) sender);
            };
            case 4 -> switch (args[1].toLowerCase()) {
                case "seticon" -> cgm.getAllowedIcons().stream().map(Material::toString).toList();
                case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                case "addcamera","removecamera" -> getCameras(sender,args[2],args[1].equalsIgnoreCase("removecamera"));
                case "addgroup","removegroup" -> getGroups(sender,args[2],args[1].equalsIgnoreCase("removegroup"));
                default -> null;
            };
            default -> null;
        };
    }
}
