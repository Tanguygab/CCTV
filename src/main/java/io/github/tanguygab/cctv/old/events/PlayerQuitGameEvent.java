package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitGameEvent {
  public void onPlayerQuitGameEvent(PlayerQuitEvent e) {
    CCTV.get().getCameras().unviewCamera(e.getPlayer());
  }
}

