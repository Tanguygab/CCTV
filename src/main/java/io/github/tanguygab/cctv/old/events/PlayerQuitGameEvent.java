package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.old.functions.camerafunctions;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitGameEvent {
  public void onPlayerQuitGameEvent(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    camerafunctions.unviewPlayer(player);
  }
}

