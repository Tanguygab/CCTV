package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.entities.Camera;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CameraCmd extends Command<Camera> {

    public CameraCmd() {
        super("camera");
    }

    public void onCommand(CommandSender sender, String[] args) {

    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

}
