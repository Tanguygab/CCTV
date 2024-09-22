package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CameraCmd extends Command<Camera> {

    private final CameraManager cm = cctv.getCameras();
    @Getter(AccessLevel.PROTECTED) private final String notFound = lang.CAMERA_NOT_FOUND;

    public CameraCmd() {
        super("camera");
    }

    @Override
    protected Camera get(String name) {
        return cm.get(name);
    }

    @Override
    protected String getOwner(Camera camera) {
        return camera.getOwner();
    }
    @Override
    protected void setOwner(Camera camera, String name) {
        camera.setOwner(name);
    }

    public void onCommand(CommandSender sender, String[] args) {

        switch (getFirstArg(args)) {
            case "get" -> {
                Player p = getPlayer(sender);
                if (p == null) return;

                if (noPerm(p, "get")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                String skin = null;
                if (args.length > 2) {
                    String[] skinArg = Arrays.copyOfRange(args,2,args.length);
                    skin = String.join(" ",skinArg);
                }
                p.getInventory().addItem(cctv.getCustomHeads().get(skin));
                p.sendMessage(lang.CAMERA_ITEM_PLACE);
            }
            case "create" -> {
                Player p = getPlayer(sender);
                if (p == null) return;

                if (noPerm(p, "create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length < 3) {
                    p.sendMessage(lang.COMMANDS_PROVIDE_NAME);
                    return;
                }
                String skin = "_DEFAULT_";
                if (args.length > 3) {
                    String[] skinArg = Arrays.copyOfRange(args,3,args.length);
                    skin = String.join(" ",skinArg);
                }
                cm.create(args[2],p.getLocation(),p,skin);
            }
            case "list" -> listCmd(sender,lang.COMMANDS_LIST_CAMERAS,cm.get((Player) sender),args);
            case "connected" -> {
                Camera camera = checkExist(sender,args);
                if (camera != null)
                    sender.sendMessage(lang.getCameraViewCount(Math.toIntExact(cctv.getViewers().values().stream().filter(viewer->viewer.getCamera()==camera).count()),camera.getName()));
            }
            case "disconnect" -> {
                if (noPerm(sender,"disconnect")) {
                    sender.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2 && sender.hasPermission("cctv.camera.disconnect.other")) {
                    Player target = Bukkit.getServer().getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(lang.PLAYER_NOT_FOUND);
                        return;
                    }
                    cm.disconnectFromCamera(target);
                    return;
                }
                Player player = getPlayer(sender);
                if (player == null) return;
                cm.disconnectFromCamera(player);
            }
            case "teleport" -> {
                Player p = getPlayer(sender);
                if (p == null) return;

                if (noPerm(p, "teleport")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                Camera camera = checkExist(p,args);
                if (camera != null) p.teleport(camera.getArmorStand());
            }
            case "movehere" -> {
                Player p = getPlayer(sender);
                if (p == null) return;

                Camera camera = checkExist(p,args);
                if (camera == null) return;
                camera.setLocation(p.getLocation());
                p.sendMessage(lang.CAMERA_MOVED);
            }
            case "moveto" -> {
                Camera camera = checkExist(sender,args);
                if (camera == null) return;

                if (noPerm(sender, "moveto")) {
                    sender.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }

                if (args.length < 6) {
                    sender.sendMessage("§cYou have to provide x, y and z coordinates!");
                    return;
                }

                try {
                    double x = Double.parseDouble(args[3]);
                    double y = Double.parseDouble(args[4]);
                    double z = Double.parseDouble(args[5]);
                    float yaw = Float.parseFloat(args[6]);
                    float pitch = Float.parseFloat(args[7]);

                    World world = null;
                    if (args.length > 8) world = Bukkit.getServer().getWorld(args[8]);
                    else if (sender instanceof Player p) world = p.getWorld();

                    if (world == null) {
                        sender.sendMessage("§cYou have to provide a world!");
                        return;
                    }

                    camera.setLocation(new Location(world, x, y, z, yaw, pitch));
                    sender.sendMessage(lang.CAMERA_MOVED);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid coordinates!");
                }
            }
            case "rename" -> {
                Camera camera = renameCmd(sender,args);
                if (camera == null) return;
                String newName = args[3];
                if (camera.rename(newName)) {
                    sender.sendMessage(lang.getCameraRenamed(newName));
                    camera.getBossbar().setTitle(camera.getName());
                    return;
                }
                sender.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            }
            case "setowner" -> {
                String owner = setOwnerCmd(sender,args,lang.CAMERA_PLAYER_ALREADY_OWNER);
                if (owner != null) sender.sendMessage(lang.getCameraOwnerChanged(owner));
            }
            case "killall" -> {
                if (!sender.hasPermission("cctv.admin")) {
                    sender.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                int i = 0;
                if (sender instanceof Player player) {
                    for (Entity entity : player.getWorld().getEntities()) {
                        if ((entity instanceof ArmorStand || entity instanceof Creeper) && entity.getCustomName() != null && entity.getCustomName().startsWith("CAM-")) {
                            entity.remove();
                            i++;
                        }
                    }
                } else {
                    for (World world : Bukkit.getServer().getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if ((entity instanceof ArmorStand || entity instanceof Creeper) && entity.getCustomName() != null && entity.getCustomName().startsWith("CAM-")) {
                                entity.remove();
                                i++;
                            }
                        }
                    }
                }
                sender.sendMessage(i+" entities removed.");
            }
            case "view" -> {
                if (noPerm(sender,"view")) {
                    sender.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                Camera camera = checkExist(sender,args);
                if (camera == null) return;
                if (args.length > 3 && sender.hasPermission("cctv.camera.view.other")) {
                    Player target = Bukkit.getServer().getPlayer(args[3]);
                    if (target == null) {
                        sender.sendMessage(lang.PLAYER_NOT_FOUND);
                        return;
                    }
                    cm.viewCamera(target,camera,null);
                    sender.sendMessage(target.getName()+" now viewing "+camera.getName());
                    return;
                }
                Player p = getPlayer(sender);
                if (p == null) return;
                cm.viewCamera(p,camera,null);
                p.sendMessage("You are now viewing "+camera.getName());
            }
            default -> helpPage(sender,"Camera commands",
                    "get:Get the camera item",
                    "create <name>:Create a new camera",
                    "list:Get the list of all cameras",
                    "connected <camera>:All players connected to this camera",
                    "disconnect [player]:Disconnect from your current camera",
                    "teleport <camera>:Teleport to the camera",
                    "movehere <camera>:Move the camera to your location",
                    "moveto <camera> <x> <y> <z> <yaw> <pitch> <world>:Move the camera to a location",
                    "rename <camera> <name>:Rename the camera",
                    "setowner <camera> <player>:Set the camera's owner",
                    "view <camera> [player]:View a camera or make a player view it");
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
                case "get","create" -> cctv.getCustomHeads().getHeads();
                default -> null;
            };
            default -> args[1].equalsIgnoreCase("get")
                    || args[1].equalsIgnoreCase("create")
                    ? cctv.getCustomHeads().getHeads()
                    : null;
        };
    }

}
