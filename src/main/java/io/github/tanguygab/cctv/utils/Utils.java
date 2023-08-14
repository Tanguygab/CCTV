package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.config.ConfigurationFile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

public class Utils {

    public static OfflinePlayer getOfflinePlayer(String player) {
        for (OfflinePlayer off : Bukkit.getServer().getOfflinePlayers())
            if (player.equalsIgnoreCase(off.getName()))
                return off;
        return null;
    }

    public static double distance(Location loc1, Location loc2) {
        return Math.sqrt(NumberConversions.square(loc1.getX() - loc2.getX()) + NumberConversions.square(loc1.getZ() - loc2.getZ()));
    }

    public static Location loadLocation(String id, ConfigurationFile config) {
        World world = Bukkit.getServer().getWorld(config.getString(id+".world"));
        double x = config.getDouble(id+".x",0);
        double y = config.getDouble(id+".y",0);
        double z = config.getDouble(id+".z",0);
        float pitch = config.getDouble(id+".pitch",0).floatValue();
        float yaw = config.getDouble(id+".yaw",0).floatValue();
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void giveOrDrop(Player player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty())
            player.getWorld().dropItem(player.getLocation(),item);
    }

}
