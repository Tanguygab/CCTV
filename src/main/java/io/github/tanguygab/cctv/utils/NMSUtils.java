package io.github.tanguygab.cctv.utils;

import com.mojang.authlib.GameProfile;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Viewer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;

public class NMSUtils {

    public static boolean isCompatible = true;
    private static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    public static void glow(Player viewer, Player viewed, boolean glow) {
        if (!isCompatible) return;
        EntityPlayer viewedNMS = ((CraftPlayer) viewed).getHandle();
        viewedNMS.i(glow); //setGlowingTag(boolean)
        if (!glow) {
            viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
            viewed.setSneaking(false);
        }
        sendPacket(viewer,new PacketPlayOutEntityMetadata(viewedNMS.ae(), viewedNMS.ai(), true));
    }

    public static void spawnNPC(Player player, Location loc) {
        if (!isCompatible) return;
        World world = ((CraftWorld)loc.getWorld()).getHandle();
        CraftServer server = (CraftServer) Bukkit.getServer();
        GameProfile profile = ((CraftPlayer)player).getProfile();
        EntityPlayer npc = new EntityPlayer(server.getServer(),world.getMinecraftWorld(),profile);

        npc.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        Viewer p = CCTV.get().getViewers().get(player);
        p.setNpc(npc);
        for (Player online : Bukkit.getOnlinePlayers()) spawnNPCForTarget(online, player);
    }

    public static void despawnNPC(Player p, Viewer viewer) {
        if (!isCompatible) return;
        EntityPlayer npc = (EntityPlayer) viewer.getNpc();
        try {
            PacketPlayOutEntityDestroy packet = (PacketPlayOutEntityDestroy) Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy")
                    .getConstructor(int[].class)
                    .newInstance(new int[]{npc.ae()});
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p == online) continue;
                sendPacket(online,packet);
                online.showPlayer(CCTV.get(),p);
            }
        } catch (Exception ignored) {}
    }

    public static void spawnNPCForTarget(Player player, Player target) {
        if (!isCompatible || player == target) return;
            Viewer p = CCTV.get().getViewers().get(target);
            EntityPlayer npc = (EntityPlayer) p.getNpc();
            sendPacket(player,new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            sendPacket(player,new PacketPlayOutNamedEntitySpawn(npc));
            sendPacket(player,new PacketPlayOutEntityHeadRotation(npc, (byte) (int) (p.getLoc().getYaw() * 256.0F / 360.0F)));
    }

}
