package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class NMSUtils {


    private Field getConnection;
    private Method sendPacket;

    private Method setGlow;
    private Method getId;
    private Method getDataWatcher;
    private Constructor<?> newPacketPlayOutEntityMetadata;

    private Method getEntityHandle;
    private Constructor<?> newPacketPlayOutCameraClass;

    public NMSUtils() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            getEntityHandle = Class.forName("org.bukkit.craftbukkit."+version+".entity.CraftEntity").getDeclaredMethod("getHandle");

            Class<?> entityPlayerClass;
            try {
                Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
                setGlow = entityClass.getDeclaredMethod("i",boolean.class);
                getId = entityClass.getDeclaredMethod("ae");
                getDataWatcher = entityClass.getDeclaredMethod("ai");

                newPacketPlayOutCameraClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutCamera").getConstructor(entityClass);
                newPacketPlayOutEntityMetadata = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata")
                        .getConstructor(int.class, Class.forName("net.minecraft.network.syncher.DataWatcher"),boolean.class);
                entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
            }
            catch (ClassNotFoundException e) {
                Class<?> entityClass = Class.forName("net.minecraft.server."+version+".Entity");
                setGlow = entityClass.getDeclaredMethod("setGlowingTag");
                getId = entityClass.getDeclaredMethod("getId");
                getDataWatcher = entityClass.getDeclaredMethod("getDataWatcher");

                newPacketPlayOutCameraClass = Class.forName("net.minecraft.server."+version+".PacketPlayOutCamera").getConstructor(entityClass);
                newPacketPlayOutEntityMetadata = Class.forName("net.minecraft.server."+version+".PacketPlayOutEntityMetadata")
                        .getConstructor(int.class, Class.forName("net.minecraft.server."+version+".DataWatcher"),boolean.class);
                entityPlayerClass = Class.forName("net.minecraft.server."+version+".EntityPlayer");
            }

            try {
                getConnection = entityPlayerClass.getDeclaredField("b");
                sendPacket = getConnection.getType().getDeclaredMethod("a",Class.forName("net.minecraft.network.protocol.Packet"));
            }
            catch (Exception e) {
                e.printStackTrace();
                getConnection = entityPlayerClass.getDeclaredField("connection");
                sendPacket = getConnection.getType().getDeclaredMethod("sendPacket",Class.forName("net.minecraft.server."+version+".Packet"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) throws InvocationTargetException, IllegalAccessException {
        Object handle = getEntityHandle.invoke(player);
        Object connection = getConnection.get(handle);
        sendPacket.invoke(connection,packet);
    }

    public void glow(Player viewer, Player viewed, boolean glow) {
        try {
            Object viewedNMS = getEntityHandle.invoke(viewed);
            setGlow.invoke(viewedNMS,glow);
            if (!glow) {
                viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
                viewed.setSneaking(false);
            }
            sendPacket(viewer, newPacketPlayOutEntityMetadata.newInstance(getId.invoke(viewedNMS), getDataWatcher.invoke(viewedNMS), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Map<Player, Location> oldLoc = new HashMap<>();
    private final Map<Player, Entity> oldEntity = new HashMap<>();

    public void setCameraPacket(Player p, Entity entity) {
        if (CCTV.get().getCameras().OLD_VIEW) {
            boolean view = p != entity;
            Location loc = oldLoc.get(p);
            if (view) {
                oldLoc.putIfAbsent(p,p.getLocation());
                if (oldEntity.containsKey(p))
                    p.showEntity(CCTV.get(), oldEntity.get(p));
                oldEntity.put(p,entity);
                loc = entity.getLocation();
                p.hideEntity(CCTV.get(),entity);
            } else {
                oldLoc.remove(p);
                oldEntity.remove(p);
                p.showEntity(CCTV.get(), entity);
            }
            p.teleport(loc);
            p.setInvisible(view);
            p.setAllowFlight(view);
            p.setInvulnerable(view);
            p.setCollidable(!view);
            p.setGravity(!view);
            return;
        }
        try {
            Object nmsEntity = getEntityHandle.invoke(entity);
            sendPacket(p,newPacketPlayOutCameraClass.newInstance(nmsEntity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
