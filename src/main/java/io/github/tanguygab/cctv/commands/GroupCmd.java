package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import lombok.AccessLevel;
import lombok.Getter;
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
    @Getter(AccessLevel.PROTECTED) private final String notFound = lang.GROUP_NOT_FOUND;

    public GroupCmd() {
        super("group");
    }

    @Override
    protected CameraGroup get(String name) {
        return cgm.get(name);
    }

    @Override
    protected String getOwner(CameraGroup group) {
        return group.getOwner();
    }
    @Override
    protected void setOwner(CameraGroup group, String name) {
        group.setOwner(name);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player p = getPlayer(sender);
        if (p == null) return;
        String arg = getFirstArg(args);
        switch (arg) {
            case "create" -> {
                if (noPerm(p, "create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) cgm.create(args[2],p);
                else p.sendMessage(lang.COMMANDS_PROVIDE_NAME);
            }
            case "delete" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;
                p.sendMessage(lang.getGroupDeleted(group.getName()));
                cgm.values().forEach(g -> g.removeCamera(group));
                cctv.getComputers().values().forEach(computer -> computer.removeCamera(group));
                cgm.delete(group.getName());
            }
            case "list" -> listCmd(p,lang.COMMANDS_LIST_GROUPS,cgm.get(p),args);
            case "seticon" -> {
                CameraGroup group = checkExist(p,args);
                if (group == null) return;
                if (args.length < 3) {
                    p.sendMessage(lang.GROUP_ICON_PROVIDE);
                    return;
                }
                Material material = Material.getMaterial(args[3]);
                if (material == null || !cgm.getAllowedIcons().contains(material.toString())) {
                    p.sendMessage(lang.getGroupIconInvalid(String.join(", ",cgm.getAllowedIcons())));
                    return;
                }
                group.setIcon(material);
                p.sendMessage(lang.GROUP_ICON_CHANGED);
            }
            case "setowner" -> {
                String owner = setOwnerCmd(p,args,lang.GROUP_PLAYER_ALREADY_OWNER);
                if (owner != null) p.sendMessage(lang.getGroupOwnerChanged(owner));
            }
            case "rename" -> {
                CameraGroup group = renameCmd(p,args);
                if (group == null) return;
                String newName = args[3];
                if (group.rename(newName)) {
                    p.sendMessage(lang.getGroupRenamed(newName));
                    group.getBossbar().setTitle(group.getName());
                    return;
                }
                p.sendMessage(lang.GROUP_ALREADY_EXISTS);
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
            case "addcamera","addgroup","removecamera","removegroup" -> {
                CameraGroup group = checkExist(p,args);
                if (group != null) editGroup(p,group,arg.endsWith("camera"),arg.startsWith("add"), args.length > 3 ? args[3] : null);
            }
            default -> helpPage(p,"Group commands",
                    "create <name>:Create a new group",
                    "delete <group>:Delete a group",
                    "list:Get the list of all groups",
                    "seticon <group> <icon>:Set the group's icon",
                    "setowner <group> <player>:Set the group's owner",
                    "rename <group> <name>:Rename the group",
                    "info <group>:Get the group's info",
                    //"addgroup <group> <group>:Add a group to your group",
                    //"removegroup <group> <group>:Remove a group from your group",
                    "addcamera <group> <camera>:Add a camera to your group",
                    "removecamera <group> <camera>:Remove a camera from your group");
        }
    }
    private void editGroup(Player player, CameraGroup group, boolean isCam, boolean add, String name) {
        if (!isCam) {
            player.sendMessage(lang.UNSUPPORTED);
            return;
        }

        if (name == null) {
            player.sendMessage(lang.getCommandsProvideCameraName());//isCam ? lang.getCommandsProvideCameraName() : lang.getCommandsProvideGroupName());
            return;
        }
        if (!cm.exists(name)) {//isCam ? !cm.exists(name) : !cgm.exists(name)) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);//isCam ? lang.CAMERA_NOT_FOUND : getNotFound());
            return;
        }
        Computable c = cm.get(name);//isCam ? cm.get(name) : cgm.get(name);
        if (add == group.getCameras().contains(c)) {
            player.sendMessage(lang.getEditCameras(add,false,true/*isCam*/,false));
            return;
        }
        if (add) group.addCamera(c);
        else group.removeCamera(c);
        player.sendMessage(lang.getEditCameras(add,true,true/*isCam*/,false));
    }

    /*private List<String> getGroups(CommandSender sender, String group, boolean toRemove) {
        if (!cgm.exists(group)) return List.of();
        List<String> added = cgm.get(group).getCameras().stream().filter(c->c instanceof CameraGroup).map(Computable::getName).toList();
        if (toRemove) return added;

        List<String> list = cgm.get((Player) sender);
        list.removeAll(added);
        return list;
    }*/
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
            case 2 -> List.of("create","delete","list","seticon","setowner","rename","info",/*"addgroup","removegroup",*/"addcamera","removecamera");
            case 3 -> switch (args[1].toLowerCase()) {
                case "create","list" -> null;
                default -> cgm.get((Player) sender);
            };
            case 4 -> switch (args[1].toLowerCase()) {
                case "seticon" -> cgm.getAllowedIcons();
                case "addcamera","removecamera" -> getCameras(sender,args[2],args[1].equalsIgnoreCase("removecamera"));
                //case "addgroup","removegroup" -> getGroups(sender,args[2],args[1].equalsIgnoreCase("removegroup"));
                default -> null;
            };
            default -> null;
        };
    }
}
