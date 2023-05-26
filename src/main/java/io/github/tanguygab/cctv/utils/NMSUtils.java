package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public class NMSUtils {

    private void sendPacket(Player player, Packet<PacketListenerPlayOut> packet) {
        ((CraftPlayer)player).getHandle().b.a(packet);
    }

    public void glow(Player viewer, Player viewed, boolean glow) {
        EntityPlayer viewedNMS = ((CraftPlayer) viewed).getHandle();
        viewedNMS.i(glow);
        sendPacket(viewer, new PacketPlayOutEntityMetadata(viewedNMS.af(),
                viewedNMS.aj().c()));
        if (!glow) {
            viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
            viewed.setSneaking(false);
        }
    }

    private final Map<Player, Location> oldLoc = new HashMap<>();
    private final Map<Player, Entity> oldEntity = new HashMap<>();

    public void setCameraPacket(Player p, Entity entity) {
        if (CCTV.get().getCameras().EXPERIMENTAL_VIEW) {
            sendPacket(p,new PacketPlayOutCamera(((CraftEntity)entity).getHandle()));
            return;
        }

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
        if (view) {
            p.teleport(loc);
            p.setAllowFlight(true);
            p.setFlying(true);
        } else {
            if (p.getGameMode() != GameMode.CREATIVE || p.getGameMode() != GameMode.SPECTATOR) {
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
