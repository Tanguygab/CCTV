package io.github.tanguygab.cctv.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;


public class NMSUtils {

    private static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    public static void glow(Player viewer, Player viewed, boolean glow) {
        EntityPlayer viewedNMS = ((CraftPlayer) viewed).getHandle();
        viewedNMS.i(glow); //setGlowingTag(boolean)
        if (!glow) {
            viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
            viewed.setSneaking(false);
        }
        sendPacket(viewer,new PacketPlayOutEntityMetadata(viewedNMS.ae(), viewedNMS.ai(), true));
    }

    public static void setCameraPacket(Player p, Entity entity) {
        sendPacket(p,new PacketPlayOutCamera(((CraftEntity)entity).getHandle()));
    }

}
