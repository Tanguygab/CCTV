package io.github.tanguygab.cctv.old.library;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflect {
  public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    String name = "net.minecraft.server." + version + nmsClassString;
    Class<?> nmsClass = Class.forName(name);
    return nmsClass;
  }
  
  public static Class<?> getCraftBukkitClass(String nmsClassString) throws ClassNotFoundException {
    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    String name = "org.bukkit.craftbukkit." + version + nmsClassString;
    Class<?> nmsClass = Class.forName(name);
    return nmsClass;
  }
  
  public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method getHandle = player.getClass().getMethod("getHandle", new Class[0]);
    Object nmsPlayer = getHandle.invoke(player, new Object[0]);
    Field conField = nmsPlayer.getClass().getField("playerConnection");
    Object con = conField.get(nmsPlayer);
    return con;
  }

}

