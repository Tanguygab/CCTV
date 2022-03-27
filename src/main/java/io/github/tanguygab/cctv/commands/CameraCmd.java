package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CameraCmd extends Command {

    private final CameraManager cm = cctv.getCameras();

    public CameraCmd() {
        super("camera");
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
                String skin = "_DEFAULT_";
                if (args.length > 2) {
                    String[] skinArg = Arrays.copyOfRange(args,2,args.length);
                    skin = String.join(" ",skinArg).replace("Default","_DEFAULT_");
                }
                p.getInventory().addItem(cctv.getCustomHeads().get(skin));
                p.sendMessage(ChatColor.GREEN + "Place down this item to create a camera!");
            }
            case "create" -> {
                if (noPerm(p, "create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) cm.create(args[2],p.getLocation(),p,"_DEFAULT_");
                else p.sendMessage(ChatColor.RED + "Please specify a camera name!");
            }
            case "delete" -> {
                if (noPerm(p, "delete")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                cm.delete(camera.getId(),p);
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
                p.spigot().sendMessage(list("Cameras",cm.get(p),"view","Click to view!",page));
            }
            case "view" -> {
                if (noPerm(p, "view")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (cctv.getViewers().exists(p)) {
                    p.sendMessage(ChatColor.RED + "You already watching a camera!");
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                cm.viewCamera(p, camera, null);
            }
            case "connected" -> {
                if (noPerm(p, "connected")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                p.sendMessage(lang.getCameraViewCount(Math.toIntExact(cctv.getViewers().values().stream().filter(viewer->viewer.getCamera()==camera).count()),camera.getId()));
            }
            case "return" -> {
                if (noPerm(p, "return")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                cm.unviewCamera(p);
            }
            case "teleport" -> {
                if (noPerm(p, "teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                cm.teleport(camera,p);
            }
            case "enable" -> {
                if (noPerm(p, "enable")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                if (camera.isEnabled()) {
                    p.sendMessage(lang.CAMERA_ALREADY_ENABLED);
                    return;
                }
                camera.setEnabled(true);
                p.sendMessage(lang.getCameraEnabled(camera.getId()));
            }
            case "disable" -> {
                if (noPerm(p, "disable")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                if (!camera.isEnabled()) {
                    p.sendMessage(lang.CAMERA_ALREADY_DISABLED);
                    return;
                }
                camera.setEnabled(false);
                p.sendMessage(lang.getCameraDisabled(camera.getId()));
            }
            case "show" -> {
                if (noPerm(p, "show")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                if (camera.isShown()) {
                    p.sendMessage(lang.CAMERA_ALREADY_SHOWN);
                    return;
                }
                camera.setShown(true);
                p.sendMessage(lang.getCameraShown(camera.getId()));
            }
            case "hide" -> {
                if (noPerm(p, "hide")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                if (!camera.isShown()) {
                    p.sendMessage(lang.CAMERA_ALREADY_HIDDEN);
                    return;
                }
                camera.setShown(false);
                p.sendMessage(lang.getCameraHidden(camera.getId()));
            }
            case "movehere" -> {
                if (noPerm(p, "movehere")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                camera.setLocation(p.getLocation());
                p.sendMessage(lang.CAMERA_MOVED);
            }
            case "rename" -> {
                if (noPerm(p, "rename")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                if (args.length < 4) {
                    p.sendMessage(ChatColor.RED + "Please specify a new name!");
                    return;
                }
                String newName = args[3];
                if (camera.rename(newName))
                    p.sendMessage(lang.getCameraRenamed(newName));
                else p.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            }
            case "setowner" -> {
                if (noPerm(p, "setowner")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Please specify a camera name!");
                    return;
                }
                Camera camera = cm.get(args[2]);
                if (camera == null || cantUse(p, camera.getOwner())) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
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
                if (camera.getOwner().equals(uuid)) {
                    p.sendMessage(lang.CAMERA_PLAYER_ALREADY_OWNER);
                    return;
                }
                camera.setOwner(uuid);
                p.sendMessage(lang.getCameraOwnerChanged(newOwner.getName()));
            }
            default -> sender.spigot().sendMessage(helpPage("Camera commands",
                    "get:Get the camera item",
                    "create <name>:Create a new camera",
                    "delete <name>:Delete a camera",
                    "list:Get the list of all cameras",
                    "view <camera>:View the camera",
                    "connected <camera>:All players connected to this camera",
                    "return:Stop viewing your current camera",
                    "teleport <camera>:Teleport to the camera",
                    "enable <camera>:Enable the camera",
                    "disable <camera>:Disable the camera",
                    "show <camera>:Show the camera",
                    "hide <camera>:Hide the camera",
                    "movehere <camera>:Move the camera to your location",
                    "rename <camera> <name>:Rename the camera",
                    "setowner <camera> <player>:Set the camera's owner"));
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("get","create","delete","list","view","connected","return","teleport","enable","disable","show","hide","movehere","rename","setowner");
            case 3 -> switch (args[1].toLowerCase()) {
                case "create","list","return" -> null;
                case "get" -> cctv.getCustomHeads().getHeads();
                default -> sender instanceof Player p ? cm.get(p) : Utils.list(cm.values());
            };
            case 4 -> switch (args[1].toLowerCase()) {
                case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                case "get" -> cctv.getCustomHeads().getHeads();
                default -> null;
            };
            default -> {
                if (args[1].equalsIgnoreCase("get"))
                    yield cctv.getCustomHeads().getHeads();
                yield null;
            }
        };
    }

}
