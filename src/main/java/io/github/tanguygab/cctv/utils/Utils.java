package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.entities.ID;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.List;
import java.util.Random;

public class Utils {

    public static NamespacedKey cameraKey = new NamespacedKey(CCTV.get(), "camera");
    public static NamespacedKey computerKey = new NamespacedKey(CCTV.get(), "computer");
    private static final Random random = new Random();

    public static int getRandomNumber(int size, String type) {
        int number = random.nextInt(size);
        switch (type.toLowerCase()) {
            case "camera" -> {
                CameraManager manager = CCTV.get().getCameras();
                if (manager.values().size() >= size) size *= 10;
                for (Camera cam : manager.values()) {
                    if (cam.getId().equals(number+""))
                        number = getRandomNumber(size, type);
                }
                return number;
            }
            case "computer" -> {
                ComputerManager manager = CCTV.get().getComputers();
                if (manager.values().size() >= size) size *= 10;
                for (Computer computer : manager.values()) {
                    if (computer.getId().equals(number+""))
                        number = getRandomNumber(size, type);
                }
                return number;
            }
            case "group" -> {
                CameraGroupManager manager = CCTV.get().getCameraGroups();
                if (manager.values().size() >= size) size *= 10;
                for (CameraGroup group : manager.values()) {
                    if (group.getId().equals(number+""))
                        number = getRandomNumber(size, type);
                }
                return number;
            }
        }
        return number;
    }

    public static OfflinePlayer getOfflinePlayer(String player) {
        for (OfflinePlayer off : Bukkit.getServer().getOfflinePlayers()) {
            if (player.equalsIgnoreCase(off.getName()))
                return off;
        }
        return null;
    }

    public static List<String> list(List<?> list) {
        return list.stream().map(el->el instanceof ID id ? id.getId() : el+"").toList();
    }

    public static double distance(Location loc1, Location loc2) {
        return Math.sqrt(NumberConversions.square(loc1.getX() - loc2.getX()) + NumberConversions.square(loc1.getZ() - loc2.getZ()));
    }

}
