package io.github.tanguygab.cctv.utils;

import com.mojang.authlib.GameProfile;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Viewer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NPCUtils {

    public static void spawn(Player player, Location loc) {
        World world = ((CraftWorld)loc.getWorld()).getHandle();
        CraftServer server = (CraftServer) Bukkit.getServer();
        GameProfile profile = ((CraftPlayer)player).getProfile();
        EntityPlayer npc = new EntityPlayer(server.getServer(),world.getMinecraftWorld(),profile);

        npc.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        Viewer p = CCTV.get().getViewers().get(player);
        p.setNpc(npc);
        for (Player online : Bukkit.getOnlinePlayers()) spawnForTarget(online, player);
    }

    public static void despawn(Player p, EntityPlayer npc) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(npc.ae());
        for (Player online : Bukkit.getOnlinePlayers()) {
            EntityPlayer playerNMS = ((CraftPlayer)online).getHandle();
            PlayerConnection connection = playerNMS.b;
            connection.a(packet);

            PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(playerNMS.ae(),playerNMS.ai(),true);
            ((CraftPlayer)p).getHandle().b.a(entityMetadata);
            online.showPlayer(CCTV.get(),p);
        }
    }

    public static void spawnForTarget(Player player, Player target) {
        Viewer p = CCTV.get().getViewers().get(target);
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = nmsPlayer.b;
        EntityPlayer npc = p.getNpc();

        PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,nmsPlayer);
        connection.a(infoPacket);

        PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
        connection.a(namedEntitySpawn);

        PacketPlayOutEntityHeadRotation entityHeadRotation = new PacketPlayOutEntityHeadRotation(npc,(byte) (int) (p.getLoc().getYaw() * 256.0F / 360.0F));
        connection.a(entityHeadRotation);

        DataWatcher dw = npc.ai();
        //DataWatcherObject<Byte> dwo = new DataWatcherObject<>(16, DataWatcherRegistry.a);
        //dw.a(dwo,(byte)79);
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(npc.ae(),dw,true);
        connection.a(entityMetadata);
    }

}
