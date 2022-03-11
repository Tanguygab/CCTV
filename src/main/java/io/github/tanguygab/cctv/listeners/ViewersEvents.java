package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

public class ViewersEvents implements Listener {

    private final LanguageFile lang;
    private final CameraManager cm;
    private final ViewerManager vm;

    public ViewersEvents() {
        lang = CCTV.get().getLang();
        cm = CCTV.get().getCameras();
        vm = CCTV.get().getViewers();
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        if (vm.exists(e.getPlayer()) && !CCTV.get().getConfiguration().getBoolean("allowed_to_chat",false))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!vm.exists(p)) return;

        p.setAllowFlight(true);
        p.setFlying(true);
        camerafunctions.teleportToCamera(vm.get(p).getCamera().getId(), p);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!vm.exists(player)) return;

        player.sendTitle("", lang.CAMERA_DISCONNECTING, 0, 15, 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> cm.unviewCamera(player),  CCTV.get().TIME_TO_DISCONNECT * 20L);
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent e) {

    }
}
