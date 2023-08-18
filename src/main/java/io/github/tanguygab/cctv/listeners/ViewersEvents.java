package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewersEvents implements Listener {

    private final CCTV cctv;
    private final LanguageFile lang;
    private final CameraManager cm;
    private final ViewerManager vm;

    public ViewersEvents(CCTV cctv) {
        this.cctv = cctv;
        lang = cctv.getLang();
        cm = cctv.getCameras();
        vm = cctv.getViewers();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if (vm.exists(e.getPlayer()) && !vm.CAN_CHAT)
            e.setCancelled(true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!vm.exists(player)) return;

        player.sendTitle(" ", lang.CAMERA_DISCONNECTING, 0, vm.TIME_TO_DISCONNECT*20, 0);
        cctv.getServer().getScheduler().scheduleSyncDelayedTask(cctv, () -> cm.disconnectFromCamera(player),  vm.TIME_TO_DISCONNECT * 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (vm.exists(p)) vm.viewersQuit.put(p.getUniqueId(), cctv.getNms().oldLoc.get(p));
        cm.disconnectFromCamera(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        if (vm.exists(e.getWhoClicked().getUniqueId().toString()))
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (vm.exists(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (vm.exists(p)) e.setCancelled(true);
            return;
        }
        if (!(e.getDamager() instanceof Player p)) {
            if (e.getEntity() instanceof ArmorStand as && cm.values().stream().anyMatch(cam -> cam.getArmorStand() == as))
                e.setCancelled(true);
            return;
        }

        if (vm.exists(p)) {
            vm.onCameraItems(p, p.getInventory().getItemInMainHand());
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (vm.exists(p)) {
            if (!e.getKeepInventory()) {
                e.getDrops().clear();
                e.getDrops().addAll(Arrays.asList(vm.get(p).getInventory()));
            }
            vm.delete(p);
            if (!e.getKeepInventory())
                p.getInventory().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        e.setCancelled(cm.connecting.contains(e.getPlayer()) || vm.exists(e.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (vm.exists(p) && vm.blockedCommands.contains(e.getMessage().split(" ")[0])) {
            e.setCancelled(true);
            p.sendMessage(lang.COMMAND_BLOCKED);
        }
    }

    private final List<Player> clickers = new ArrayList<>(); 
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!cctv.getViewers().exists(player)) return;
        e.setCancelled(true);
        if (e.getHand() != EquipmentSlot.OFF_HAND && !clickers.contains(player)) {
            cctv.getViewers().onCameraItems(player, e.getItem());
            clickers.add(player);
            cctv.getServer().getScheduler().runTaskLater(cctv,()->clickers.remove(player),3);
        }
    }
}
