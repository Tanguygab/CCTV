package io.github.tanguygab.cctv.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CameraCmd extends Command {

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
            case "get" -> {}
            case "create" -> {}
            case "delete" -> {}
            case "list" -> {}
            case "view" -> {}
            case "connected" -> {}
            case "return" -> {}
            case "teleport" -> {}
            case "enable" -> {}
            case "disable" -> {}
            case "show" -> {}
            case "hide" -> {}
            case "movehere" -> {}
            case "rename" -> {}
            case "setowner" -> {}
            default -> sender.spigot().sendMessage(helpPage("Camera commands",
                    "get:Get the camera item",
                    "create <name>:Create a new camera",
                    "delete <name>:Delete a camera",
                    "list:Get the list of all computers",
                    "view <camera>:View the camera",
                    "connected <camera>:All players connected to this camera",
                    "return:Stop viewing your current camera",
                    "teleport <computer>:Teleport to the camera",
                    "enable <camera>:Enable the camera",
                    "disable <camera>:Disable the camera",
                    "show <camera>:Show the camera",
                    "hide <camera>:Hide the camera",
                    "movehere:Move the camera to your location",
                    "rename <camera> <name>:Rename the camera",
                    "setowner <camera> <player>:Set the camera's owner"));

        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

}
