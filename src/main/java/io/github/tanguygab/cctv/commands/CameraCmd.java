package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
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
        Player p = getPlayer(sender);
        if (p == null) return;

        switch (getFirstArg(args)) {
            case "get" -> {
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
            case "list" -> listCmd(p,lang.COMMANDS_LIST_CAMERAS,cm.get(p),args);
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
                Camera camera = renameCmd(p,args);
                if (camera == null) return;
                String newName = args[3];
                if (camera.rename(newName)) {
                    p.sendMessage(lang.getCameraRenamed(newName));
                    camera.getBossbar().setTitle(camera.getName());
                    return;
                }
                p.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            }
            case "setowner" -> {
                String owner = setOwnerCmd(p,args,lang.CAMERA_PLAYER_ALREADY_OWNER);
                if (owner != null) p.sendMessage(lang.getCameraOwnerChanged(owner));
            }
            case "killall" -> {
                if (!p.hasPermission("cctv.admin")) {
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
            case "view" -> {
                if (noPerm(p,"view")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                Camera camera = checkExist(p,args);
                if (camera == null) return;
                if (args.length > 3 && p.hasPermission("cctv.camera.view.other")) {
                    Player target = Bukkit.getServer().getPlayer(args[3]);
                    if (target == null) {
                        p.sendMessage(lang.PLAYER_NOT_FOUND);
                        return;
                    }
                    cm.viewCamera(target,camera,null);
                    p.sendMessage(target.getName()+" now viewing "+camera.getName());
                    return;
                }
                cm.viewCamera(p,camera,null);
                p.sendMessage("You are now viewing "+camera.getName());
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
