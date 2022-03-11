package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerBlockBreakEvent {
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if (event.getBlock().getType().equals(ComputerManager.COMPUTER_MATERIAL))
      for (Computer rec : CCTV.get().getComputers().values()) {
        if ((rec.getOwner().equals(event.getPlayer().getUniqueId().toString()) || event.getPlayer().hasPermission("cctv.computer.other")) && rec.getLocation().equals(event.getBlock().getLocation())) {
          event.getBlock().setType(Material.AIR);
          ItemStack computer = new ItemStack(ComputerManager.COMPUTER_MATERIAL);
          ItemMeta computerMeta = computer.getItemMeta();
          computerMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Computer"));
          computer.setItemMeta(computerMeta);
          if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.getPlayer().getInventory().addItem(computer);
          event.setCancelled(true);
          computerfunctions.deleteComputer(event.getPlayer(), rec.getId());
          return;
        } 
      }  
  }
}
