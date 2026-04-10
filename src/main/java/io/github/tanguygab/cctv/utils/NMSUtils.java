package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NMSUtils {

    private ServerPlayer getHandle(Player player) {
        return ((CraftPlayer)player).getHandle();
    }

    public void glow(Player viewer, Player target, boolean glow) {
        ServerPlayer handle = getHandle(target);
        handle.setGlowingTag(glow);
        handle.refreshEntityData(getHandle(viewer));
    }

    public final Map<Player, Location> oldLoc = new HashMap<>();
    private final Map<Player, Entity> oldEntity = new HashMap<>();

    public void setCameraPacket(Player player, Entity entity) {
        if (CCTV.getInstance().getCameras().EXPERIMENTAL_VIEW) {
            getHandle(player).connection.send(new ClientboundSetCameraPacket(((CraftEntity)entity).getHandle()));
            return;
        }

        boolean view = player != entity;
        Location loc = oldLoc.get(player);
        if (view) {
            oldLoc.putIfAbsent(player,player.getLocation());
            if (oldEntity.containsKey(player))
                player.showEntity(CCTV.getInstance(), oldEntity.get(player));
            oldEntity.put(player,entity);
            loc = entity.getLocation();
        } else {
            oldLoc.remove(player);
            oldEntity.remove(player);
            player.showEntity(CCTV.getInstance(), entity);
        }
        if (view) {
            player.teleport(loc);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.hideEntity(CCTV.getInstance(),entity);
        } else {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
            player.teleport(loc);
        }
        player.setInvisible(view);
        player.setInvulnerable(view);
        player.setCollidable(!view);
    }

    public void showViewerInTablistFor(Player viewer, Player target) {
        getHandle(target).connection.sendPacket(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(getHandle(viewer))));
    }
}
