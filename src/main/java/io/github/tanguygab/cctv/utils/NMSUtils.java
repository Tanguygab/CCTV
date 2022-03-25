package io.github.tanguygab.cctv.utils;

import com.mojang.authlib.GameProfile;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Viewer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtils {

    public static void glow(Player viewer, Player viewed, boolean glow) {
        try {
            EntityPlayer viewedNMS = ((CraftPlayer) viewed).getHandle();
            viewedNMS.i(glow); //setGlowingTag(boolean)
            if (!glow) {
                viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
                viewed.setSneaking(false);
            }
            PlayerConnection connection = ((CraftPlayer) viewer).getHandle().b;
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(viewedNMS.ae(), viewedNMS.ai(), true);
            connection.a(packet);
        } catch (Exception e) {
            // unsupported version
        }
    }

    public static void spawnNPC(Player player, Location loc) {
        try {
            World world = ((CraftWorld)loc.getWorld()).getHandle();
            CraftServer server = (CraftServer) Bukkit.getServer();
            GameProfile profile = ((CraftPlayer)player).getProfile();
            EntityPlayer npc = new EntityPlayer(server.getServer(),world.getMinecraftWorld(),profile);

            npc.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            Viewer p = CCTV.get().getViewers().get(player);
            p.setNpc(npc);
            for (Player online : Bukkit.getOnlinePlayers()) spawnNPCForTarget(online, player);
        } catch (Exception e) {
            // unsupported version
        }
    }

    public static void despawnNPC(Player p, EntityPlayer npc) {
        try {
            PacketPlayOutEntityDestroy packet = null;
            packet = (PacketPlayOutEntityDestroy) Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy").getConstructor(int[].class).newInstance(new int[]{npc.ae()});

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p == online) continue;
                EntityPlayer playerNMS = ((CraftPlayer)online).getHandle();
                PlayerConnection connection = playerNMS.b;
                connection.a(packet);
                online.showPlayer(CCTV.get(),p);
            }
        } catch (Exception e) {
            // unsupported version
        }
    }

    public static void spawnNPCForTarget(Player player, Player target) {
        if (player == target) return;
        try {
            Viewer p = CCTV.get().getViewers().get(target);
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = nmsPlayer.b;
            EntityPlayer npc = p.getNpc();

            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc);
            connection.a(infoPacket);

            PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
            connection.a(namedEntitySpawn);

            PacketPlayOutEntityHeadRotation entityHeadRotation = new PacketPlayOutEntityHeadRotation(npc, (byte) (int) (p.getLoc().getYaw() * 256.0F / 360.0F));
            connection.a(entityHeadRotation);
        } catch (Exception e) {
            // unsupported version
        }
    }

}
