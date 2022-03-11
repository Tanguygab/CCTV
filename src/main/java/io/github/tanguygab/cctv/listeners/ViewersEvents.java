package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.utils.CameraUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ViewersEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        if (CCTV.get().getViewers().exists(e.getPlayer()) && !CCTV.get().getConfiguration().getBoolean("allowed_to_chat",false))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!CCTV.get().getViewers().exists(p)) return;

        p.setAllowFlight(true);
        p.setFlying(true);
        Viewer viewer = CCTV.get().getViewers().get(p);
        camerafunctions.teleportToCamera(viewer.getCamera().getId(), p);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent e) {

    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerQuitEvent e) {
        CameraUtils.unviewCamera(e.getPlayer());
    }
}
