package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerSneakEvent {
  public void onPlayerSneakEvent(PlayerToggleSneakEvent e) {
      Player player = e.getPlayer();
      if (!CCTV.get().getViewers().exists(player)) return;

      player.sendTitle("", CCTV.get().getLang().CAMERA_DISCONNECTING, 0, 15, 0);
      Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> CCTV.get().getCameras().unviewCamera(player),  CCTV.get().TIME_TO_DISCONNECT * 20L);

  }
}

