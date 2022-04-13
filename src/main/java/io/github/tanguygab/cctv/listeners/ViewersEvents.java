package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

import java.util.Arrays;

public class ViewersEvents implements Listener {

    private final LanguageFile lang;
    private final CameraManager cm;
    private final ViewerManager vm;

    public ViewersEvents() {
        lang = CCTV.get().getLang();
        cm = CCTV.get().getCameras();
        vm = CCTV.get().getViewers();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        if (vm.exists(e.getPlayer()) && !vm.CAN_CHAT)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!vm.exists(player)) return;

        player.sendTitle(" ", lang.CAMERA_DISCONNECTING, 0, vm.TIME_TO_DISCONNECT*20, 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> cm.unviewCamera(player),  vm.TIME_TO_DISCONNECT * 20L);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        cm.unviewCamera(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryInteractEvent e) {
        if (vm.exists(e.getWhoClicked().getUniqueId().toString()))
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDropItemEvent e) {
        if (vm.exists(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent e) {
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

    @EventHandler
    public void on(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (vm.exists(p)) {
            if (!e.getKeepInventory()) {
                e.getDrops().clear();
                e.getDrops().addAll(Arrays.asList(vm.get(p).getInv()));
            }
            vm.delete(p);
            if (!e.getKeepInventory())
                p.getInventory().clear();
        }
    }
}
