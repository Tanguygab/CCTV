package io.github.tanguygab.cctv.old.library;

import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.old.events.ChatEvent;
import io.github.tanguygab.cctv.old.events.InteractEvent;
import io.github.tanguygab.cctv.old.events.MoveEvent;
import io.github.tanguygab.cctv.old.events.PlayerAttackEvent;
import io.github.tanguygab.cctv.old.events.PlayerBlockBreakEvent;
import io.github.tanguygab.cctv.old.events.PlayerBlockPlaceEvent;
import io.github.tanguygab.cctv.old.events.PlayerInventoryClickEvent;
import io.github.tanguygab.cctv.old.events.PlayerQuitGameEvent;
import io.github.tanguygab.cctv.old.events.PlayerSneakEvent;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.utils.ComputerUtils;
import io.github.tanguygab.cctv.utils.NPCUtils;
import io.github.tanguygab.cctv.utils.Utils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class EventsHandler implements Listener {
  InteractEvent interact;
  
  PlayerBlockPlaceEvent blockplace;
  
  PlayerBlockBreakEvent blockbreak;
  
  PlayerAttackEvent playerattack;
  
  PlayerQuitGameEvent playerquitgame;
  
  PlayerSneakEvent playersneak;
  
  public static PlayerInventoryClickEvent playerinventoryclick;
  
  MoveEvent playermove;
  
  ChatEvent chat;
  
  public EventsHandler() {
    this.interact = new InteractEvent();
    this.blockplace = new PlayerBlockPlaceEvent();
    this.blockbreak = new PlayerBlockBreakEvent();
    this.playerattack = new PlayerAttackEvent();
    this.playerquitgame = new PlayerQuitGameEvent();
    this.playersneak = new PlayerSneakEvent();
    playerinventoryclick = new PlayerInventoryClickEvent();
    this.playermove = new MoveEvent();
    this.chat = new ChatEvent();
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntitys(PlayerInteractAtEntityEvent event) {
    if (event.getRightClicked() instanceof org.bukkit.entity.ArmorStand) {
      if (event.getRightClicked().getCustomName() != null && ChatColor.stripColor(event.getRightClicked().getCustomName()).startsWith("CAM-"))
        event.setCancelled(true);
      Player player = event.getPlayer();
      if (CCTV.get().getViewers().exists(player)) {
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR) && (
          player.getInventory().getItemInMainHand().getType().equals(Material.getMaterial("SKULL_ITEM")) || player.getInventory().getItemInMainHand().getType().equals(Material.ENDER_PEARL) || player.getInventory().getItemInMainHand().getType().equals(Material.ENDER_EYE))) {
          ItemStack item = player.getInventory().getItemInMainHand();
          viewfunctions.switchFunctions(player, item);
        }
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteractEvent(PlayerInteractEvent event) {
    this.interact.onInteractEvent(event);
  }
  
  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent event) {
    this.blockplace.onBlockPlaceEvent(event);
  }
  
  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent event) {
    this.blockbreak.onBlockBreakEvent(event);
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerAttackEvent(EntityDamageByEntityEvent event) {
    this.playerattack.onPlayerAttackEvent(event);
  }
  
  @EventHandler
  public void onPlayerQuitGameEvent(PlayerQuitEvent event) {
    this.playerquitgame.onPlayerQuitGameEvent(event);
  }
  
  @EventHandler
  public void onPlayerSneakEvent(PlayerToggleSneakEvent event) throws Exception {
    playersneak.onPlayerSneakEvent(event);
  }
  
  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) {
    playerinventoryclick.onInventoryClickEvent(event);
  }
  
  @EventHandler
  public void Inv(InventoryInteractEvent event) {
    Player player = (Player)event.getWhoClicked();
    if (CCTV.get().getViewers().exists(player))
      event.setCancelled(true); 
  }
  
  @EventHandler
  public void onItemDropEvent(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (CCTV.get().getViewers().exists(player))
      event.setCancelled(true); 
  }
  
  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    Player joined = event.getPlayer();
    CCTV.get().getViewers().values().forEach(player->{
      Player p = Bukkit.getPlayer(UUID.fromString(player.getId()));
      joined.hidePlayer(CCTV.get(),p);
      NPCUtils.spawnForTarget(joined,p);
    });
    for (Player player : Bukkit.getOnlinePlayers()) {
      EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
      EntityPlayer nmsJoin = ((CraftPlayer) joined).getHandle();
      PlayerConnection connectionPlayer = nmsPlayer.b;
      PlayerConnection connectionJoin = nmsJoin.b;
      nmsPlayer.i(false);
      nmsJoin.i(false);
      DataWatcher dwPlayer = new DataWatcher(nmsPlayer);
      DataWatcher dwJoin = new DataWatcher(nmsJoin);
      PacketPlayOutEntityMetadata entityMetadataPlayer = new PacketPlayOutEntityMetadata(nmsPlayer.ae(), dwPlayer, true);
      PacketPlayOutEntityMetadata entityMetadataJoin = new PacketPlayOutEntityMetadata(nmsJoin.ae(), dwJoin, true);
      connectionJoin.a(entityMetadataPlayer);
      connectionPlayer.a(entityMetadataJoin);

      joined.discoverRecipe(Utils.cameraKey);
      joined.discoverRecipe(Utils.computerKey);
    }
  }
  
  @EventHandler
  public void onMoveEvent(PlayerMoveEvent event) {
    this.playermove.onMoveEvent(event);
  }
  
  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    this.chat.onChat(event);
  }
  
  @EventHandler
  public void onPistonMove(BlockPistonExtendEvent e) {
    if (e.getBlocks().stream().anyMatch(b -> computerfunctions.computerExistOnLocation(b.getLocation())))
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onPistonMove(BlockPistonRetractEvent e) {
    if (e.getBlocks().stream().anyMatch(b -> computerfunctions.computerExistOnLocation(b.getLocation())))
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onBlockExplosionEvent(BlockExplodeEvent e) {
    if (e.blockList().stream().anyMatch(b -> computerfunctions.computerExistOnLocation(b.getLocation())))
      e.blockList().removeIf(b -> b.getType().equals(ComputerUtils.getComputerMaterial()));
  }
  
  @EventHandler
  public void onEntityExplosionEvent(EntityExplodeEvent e) {
    if (e.blockList().stream().anyMatch(b -> computerfunctions.computerExistOnLocation(b.getLocation())))
      e.blockList().removeIf(b -> b.getType().equals(ComputerUtils.getComputerMaterial()));
  }
}
