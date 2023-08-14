package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CameraCmd extends Command<Camera> {

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
                if (noPerm(p, "get")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                String skin = "_DEFAULT_";
                if (args.length > 2) {
                    String[] skinArg = Arrays.copyOfRange(args,2,args.length);
                    skin = String.join(" ",skinArg);
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
            case "list" -> {
                int page = 1;
                if (args.length > 2) {
                    try {page = Integer.parseInt(args[2]);}
                    catch (Exception ignored) {}
                }
                p.spigot().sendMessage(list("Cameras",cm.get(p), "Click to view!",page));
            }
            case "connected" -> {
                Camera camera = checkExist(p,args);
                if (camera != null)
                    p.sendMessage(lang.getCameraViewCount(Math.toIntExact(cctv.getViewers().values().stream().filter(viewer->viewer.getCamera()==camera).count()),camera.getName()));
            }
            case "disconnect" -> cm.disconnectFromCamera(p);
            case "teleport" -> {
                if (noPerm(p, "teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                Camera camera = checkExist(p,args);
                if (camera != null) p.teleport(camera.getArmorStand());
            }
            case "movehere" -> {
                Camera camera = checkExist(p,args);
                if (camera == null) return;
                camera.setLocation(p.getLocation());
                p.sendMessage(lang.CAMERA_MOVED);
            }
            case "rename" -> {
                Camera camera = checkExist(p,args);
                if (camera == null) return;

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
                Camera camera = checkExist(p,args);
                if (camera == null) return;

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
            case "killall" -> {
                if (p.hasPermission("cctv.admin")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                int i = 0;
                for (Entity entity : p.getWorld().getEntities()) {
                    if ((entity instanceof ArmorStand || entity instanceof Creeper) && entity.getCustomName() != null && entity.getCustomName().startsWith("CAM-")) {
                        entity.remove();
                        i++;
                    }
                }
                p.sendMessage(i+" entities removed.");
            }
            default -> helpPage(p,"Camera commands",
                    "get:Get the camera item",
                    "create <name>:Create a new camera",
                    "list:Get the list of all cameras",
                    "connected <camera>:All players connected to this camera",
                    "disconnect:Disconnect from your current camera",
                    "teleport <camera>:Teleport to the camera",
                    "movehere <camera>:Move the camera to your location",
                    "rename <camera> <name>:Rename the camera",
                    "setowner <camera> <player>:Set the camera's owner");
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("get","create","list","connected","disconnect","teleport","movehere","rename","setowner");
            case 3 -> switch (args[1].toLowerCase()) {
                case "create","list","disconnect" -> null;
                case "get" -> cctv.getCustomHeads().getHeads();
                default -> sender instanceof Player p ? cm.get(p) : cm.values().stream().map(Camera::getName).toList();
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
