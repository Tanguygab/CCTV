package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.library.Arguments;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class Utils {

    public static NamespacedKey cameraKey = new NamespacedKey(CCTV.get(), "camera");
    public static NamespacedKey computerKey = new NamespacedKey(CCTV.get(), "computer");
    private static final Random random = new Random();

    public static ItemStack getComputer() {
        ItemStack computer = new ItemStack(ComputerUtils.getComputerMaterial());
        ItemMeta computerMeta = computer.getItemMeta();
        computerMeta.setDisplayName(ChatColor.BLUE+"Computer");
        computer.setItemMeta(computerMeta);
        return computer;
    }

    public static ItemStack getCamera() {
        ItemStack cam = Heads.CAMERA_1.get();
        ItemMeta camMeta = cam.getItemMeta();
        camMeta.setDisplayName(Arguments.camera_item_name);
        cam.setItemMeta(camMeta);
        return cam;
    }

    public static int getRandomNumber(int size, String type) {
        int number = random.nextInt(size);
        switch (type.toLowerCase()) {
            case "camera" -> {
                CameraManager manager = CCTV.get().getCameras();
                if (manager.values().size() >= size) size *= 10;
                for (Camera cam : manager.values()) {
                    if (Integer.parseInt(cam.getId()) == number)
                        number = getRandomNumber(size, type);
                }
                return number;
            }
            case "computer" -> {
                ComputerManager manager = CCTV.get().getComputers();
                if (manager.values().size() >= size) size *= 10;
                for (Computer computer : manager.values()) {
                    if (Integer.parseInt(computer.getId()) == number)
                        number = getRandomNumber(size, type);
                }
                return number;
            }
            case "group" -> {
                CameraGroupManager manager = CCTV.get().getCameraGroups();
                if (manager.values().size() >= size) size *= 10;
                for (CameraGroup group : manager.values()) {
                    if (Integer.parseInt(group.getId()) == number)
                        number = getRandomNumber(size, type);
                }
                return number;
            }
        }
        return number;
    }

    public static boolean canUse(String id, Player p, String permission) {
        return id.equals(p.getUniqueId().toString()) || p.hasPermission("cctv."+permission);
    }
}
