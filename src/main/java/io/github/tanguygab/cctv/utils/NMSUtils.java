package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NMSUtils {

    private boolean nmsSupported;
    private Method getHandle;
    private Field playerConnection;
    private Method sendPacket;
    private Method setGlow;
    private Method getId;
    private Method getDataWatcher;
    private Method getDataWatcherObjects;
    private Constructor<?> packetPlayOutCamera;
    private Constructor<?> packetPlayOutEntityMetadata;

    public NMSUtils() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Class<?> craftEntity = Class.forName("org.bukkit.craftbukkit."+version+".entity.CraftEntity");
            getHandle = craftEntity.getDeclaredMethod("getHandle");
            Class<?> entityPlayer = Class.forName("net.minecraft.server.level.EntityPlayer");
            try {playerConnection = entityPlayer.getDeclaredField("c");}
            catch (Exception e) {playerConnection = entityPlayer.getDeclaredField("b");}
            sendPacket = Class.forName("net.minecraft.server.network.PlayerConnection").getDeclaredMethod("a",
                    Class.forName("net.minecraft.network.protocol.Packet"));
            packetPlayOutCamera = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutCamera")
                    .getConstructor(Class.forName("net.minecraft.world.entity.Entity"));
            packetPlayOutEntityMetadata = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, List.class);

            setGlow = entityPlayer.getMethod("i", boolean.class);
            getId = entityPlayer.getMethod("af");

            getDataWatcher = entityPlayer.getMethod("aj");
            getDataWatcherObjects = Class.forName("net.minecraft.network.syncher.DataWatcher").getDeclaredMethod("c");

            nmsSupported = true;
        } catch (Exception e) {
            nmsSupported = false;
        }
    }

    public boolean isNMSSupported() {
        return nmsSupported;
    }

    private void sendPacket(Player player, Object packet) {
        try {
            sendPacket.invoke(playerConnection.get(getHandle.invoke(player)),packet);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void glow(Player viewer, Player viewed, boolean glow) {
        if (!nmsSupported) {
            viewer.sendMessage(CCTV.getInstance().getLang().UNSUPPORTED);
            return;
        }
        try {
            Object viewedNMS = getHandle.invoke(viewed);
            setGlow.invoke(viewedNMS,glow);
            viewer.sendMessage(viewer.getName()+" "+viewed.getName());
            sendPacket(viewer,packetPlayOutEntityMetadata.newInstance(getId.invoke(viewedNMS),
                    getDataWatcherObjects.invoke(getDataWatcher.invoke(viewedNMS))));
        } catch (Exception e) {e.printStackTrace();}
    }

    public final Map<Player, Location> oldLoc = new HashMap<>();
    private final Map<Player, Entity> oldEntity = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public void setCameraPacket(Player p, Entity entity) {
        if (CCTV.getInstance().getCameras().EXPERIMENTAL_VIEW) {
            try {
                sendPacket(p,packetPlayOutCamera.newInstance(getHandle.invoke(entity)));
            } catch (Exception e) {e.printStackTrace();}
            return;
        }

        boolean view = p != entity;
        Location loc = oldLoc.get(p);
        if (view) {
            oldLoc.putIfAbsent(p,p.getLocation());
            if (oldEntity.containsKey(p))
                p.showEntity(CCTV.getInstance(), oldEntity.get(p));
            oldEntity.put(p,entity);
            loc = entity.getLocation();
            p.hideEntity(CCTV.getInstance(),entity);
        } else {
            oldLoc.remove(p);
            oldEntity.remove(p);
            p.showEntity(CCTV.getInstance(), entity);
        }
        if (view) {
            p.teleport(loc);
            p.setAllowFlight(true);
            p.setFlying(true);
        } else {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setAllowFlight(false);
                p.setFlying(false);
            }
            p.teleport(loc);
        }
        p.setInvisible(view);
        p.setInvulnerable(view);
        p.setCollidable(!view);
    }

}
