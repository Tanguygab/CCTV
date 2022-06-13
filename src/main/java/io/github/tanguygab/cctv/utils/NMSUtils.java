package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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
        sendPacket(viewer, new PacketPlayOutEntityMetadata(viewedNMS.ae(), viewedNMS.ai(), true));
        if (!glow) {
            viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
            viewed.setSneaking(false);
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
        sendPacket(p,new PacketPlayOutCamera(((CraftEntity)entity).getHandle()));
    }

}
