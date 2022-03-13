package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CameraCmd extends Command<Camera> {

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
            case "teleport" -> {}
            case "create" -> {}
            case "delete" -> {}
            case "enable" -> {}
            case "rename" -> {}
            case "return" -> {}
            case "connected" -> {}
            case "movehere" -> {}
            case "get" -> {}
            case "hide" -> {}
            case "list" -> {}
            case "show" -> {}
            case "view" -> {}
            case "setowner" -> {}
            case "disable" -> {}
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

}
