package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GroupCmd extends Command<CameraGroup> {

    private final CameraGroupManager cgm = cctv.getGroups();
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
            case "create" -> {}
            case "list" -> {}
            case "seticon" -> {}
            case "setowner" -> {}
            case "rename" -> {}
            case "info" -> {}
            case "addgroup" -> {}
            case "removegroup" -> {}
            case "addcamera" -> {}
            case "removecamera" -> {}
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

        CameraManager cm = cctv.getCameras();
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
                case "seticon" -> List.of("CHEST","ENDER_CHEST","SHULKER_BOX","BARREL");
                case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                case "addcamera","removecamera" -> getCameras(sender,args[2],args[1].equalsIgnoreCase("removecamera"));
                case "addgroup","removegroup" -> getGroups(sender,args[2],args[1].equalsIgnoreCase("removegroup"));
                default -> null;
            };
            default -> null;
        };
    }
}
