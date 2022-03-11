package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ComputerUtils {

    public static Map<String, Computer> computers = new HashMap<>();
    public static Material COMPUTER_MATERIAL;

    public static Material getComputerMaterial() {
        return COMPUTER_MATERIAL;
    }

}
