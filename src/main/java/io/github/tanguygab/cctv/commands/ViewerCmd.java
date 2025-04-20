package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.menus.ViewerOptionsMenu;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ViewerCmd extends Command<Camera> {

    private final CameraManager cm = cctv.getCameras();
    private final ViewerManager vm = cctv.getViewers();
    @Getter(AccessLevel.PROTECTED) private final String notFound = null;

    public ViewerCmd() {
        super("viewer");
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return;
        }
        if (!vm.exists(player)) {
            sender.sendMessage("§cYou must be viewing a camera to use this command.");
            return;
        }

        Camera camera = vm.get(player).getCamera();
        String arg2 = args.length > 2 ? args[2].toLowerCase() : "";

        switch (getFirstArg(args)) {
            case "options" -> cctv.openMenu(player, new ViewerOptionsMenu(player));
            case "rotate" -> cm.rotate(player, camera, arg2.equals("left") ? -18 : 18,true);
            case "switch" -> vm.switchCamera(player, arg2.equals("previous"));
            case "disconnect" -> {
                player.sendTitle(" ", cctv.getLang().CAMERA_DISCONNECTING, 0, vm.TIME_TO_DISCONNECT*20, 0);
                Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> cm.disconnectFromCamera(player),  vm.TIME_TO_DISCONNECT * 20L);
            }
            default -> helpPage(sender,"Viewer commands",
                    "options:Open the options menu",
                    "rotate <left|right>:Rotate temporarily the camera to the left/right",
                    "switch <previous|next>:Switch to the previous/next camera",
                    "disconnect:Disconnect from the camera"
            );
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> List.of("options", "rotate", "switch", "disconnect");
            case 3  -> switch (getFirstArg(args)) {
                case "rotate" -> List.of("left", "right");
                case "switch" -> List.of("previous", "next");
                default -> List.of();
            };
            default -> List.of();
        };
    }

}
