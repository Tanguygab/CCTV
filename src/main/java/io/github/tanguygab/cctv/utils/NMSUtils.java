package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NMSUtils {

    public static final PotionEffectType SLOWNESS = getSlowness();
    @Getter private boolean nmsSupported;
    private Method getHandle;
    private Field playerConnection;
    private Method sendPacket;
    private Method setGlow;
    private Method getId;
    private Method getDataWatcher;
    private Method getDataWatcherObjects;
    private Constructor<?> packetPlayOutCamera;
    private Constructor<?> packetPlayOutEntityMetadata;
    private boolean oldMetadataPacket = false;

    public NMSUtils() {
        String[] version = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        try {
            Class<?> craftEntity = Class.forName("org.bukkit.craftbukkit."+(version.length > 3 ? version[3] + "." : "")+"entity.CraftEntity");
            getHandle = craftEntity.getDeclaredMethod("getHandle");
            Class<?> entityPlayer = Class.forName("net.minecraft.server.level.EntityPlayer");
            Class<?> playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
            try {
                playerConnection = entityPlayer.getDeclaredField("b");
                if (playerConnection.getType() != playerConnectionClass)
                    playerConnection = entityPlayer.getDeclaredField("c");
            }
            catch (Exception e) {playerConnection = entityPlayer.getDeclaredField("b");}
            sendPacket = playerConnectionClass.getDeclaredMethod("a",
                    Class.forName("net.minecraft.network.protocol.Packet"));
            packetPlayOutCamera = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutCamera")
                    .getConstructor(Class.forName("net.minecraft.world.entity.Entity"));

            Class<?> dataWatcher = Class.forName("net.minecraft.network.syncher.DataWatcher");
            try {
                packetPlayOutEntityMetadata = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata")
                        .getConstructor(int.class, List.class);
            } catch (Exception e) {
                packetPlayOutEntityMetadata = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata")
                        .getConstructor(int.class, dataWatcher, boolean.class);
                oldMetadataPacket = true;
            }

            setGlow = tryThen(entityPlayer, void.class, List.of("setGlowingTag","i","j"),boolean.class);
            getId = tryThen(entityPlayer, int.class, List.of("getId","an","al","aj","ah","af","ae"));
            getDataWatcher = tryThen(entityPlayer,dataWatcher,List.of("getEntityData","ar","ap","an","al","aj","ai"));
            if (!oldMetadataPacket) getDataWatcherObjects = dataWatcher.getDeclaredMethod("c");

            nmsSupported = true;
        } catch (Exception e) {
            nmsSupported = false;
        }
    }

    private static PotionEffectType getSlowness() {
        PotionEffectType effect = PotionEffectType.getByKey(NamespacedKey.minecraft("slowness"));
        return effect != null ? effect : PotionEffectType.SLOWNESS;
    }

    private Method tryThen(Class<?> clazz, Class<?> returnedClass, List<String> methods, Class<?>... args) throws NoSuchMethodException {
        for (String method : methods)
            try {
                Method m = clazz.getMethod(method,args);
                if (m.getReturnType() == returnedClass) return m;
            }
            catch (Exception ignored) {}
        throw new NoSuchMethodException(methods.toString());
    }

    private void sendPacket(Player player, Object packet) {
        try {
            sendPacket.invoke(playerConnection.get(getHandle.invoke(player)),packet);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void glow(Player viewer, Player viewed, boolean glow) {
        try {
            Object viewedNMS = getHandle.invoke(viewed);
            setGlow.invoke(viewedNMS,glow);
            sendPacket(viewer,
                    oldMetadataPacket
                            ? packetPlayOutEntityMetadata.newInstance(
                                    getId.invoke(viewedNMS),
                                    getDataWatcher.invoke(viewedNMS),
                                    true
                            ) : packetPlayOutEntityMetadata.newInstance(
                                    getId.invoke(viewedNMS),
                                    getDataWatcherObjects.invoke(getDataWatcher.invoke(viewedNMS))
                            )
            );
        } catch (Exception e) {e.printStackTrace();}
    }

    public final Map<Player, Location> oldLoc = new HashMap<>();
    private final Map<Player, Entity> oldEntity = new HashMap<>();

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
