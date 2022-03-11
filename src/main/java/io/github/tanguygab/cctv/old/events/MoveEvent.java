package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent {
  @EventHandler
  public void onMoveEvent(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    ViewerManager vm = CCTV.get().getViewers();
    if (vm.exists(player)) {
      Viewer viewer = vm.get(player);
      player.setAllowFlight(true);
      player.setFlying(true);
      camerafunctions.teleportToCamera(viewer.getCamera().getId(), player);
    } 
  }
}

