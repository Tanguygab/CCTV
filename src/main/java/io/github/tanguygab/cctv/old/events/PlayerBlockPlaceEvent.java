package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerBlockPlaceEvent {
  public void onBlockPlaceEvent(BlockPlaceEvent event) {
    if (event.getItemInHand().hasItemMeta() && event.getItemInHand().getItemMeta().hasDisplayName() && event.getItemInHand().getItemMeta().getDisplayName().contains("Computer"))
      CCTV.get().getComputers().create("",event.getPlayer(), event.getBlock().getLocation());
  }
}