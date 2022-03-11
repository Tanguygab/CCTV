package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.old.records.ComputerRecord;
import io.github.tanguygab.cctv.utils.ComputerUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerBlockBreakEvent {
  public void onBlockBreakEvent(BlockBreakEvent event) {
    if (event.getBlock().getType().equals(ComputerUtils.getComputerMaterial()))
      for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
        if ((rec.owner.equals(event.getPlayer().getUniqueId().toString()) || event.getPlayer().hasPermission("cctv.computer.other")) && rec.loc.equals(event.getBlock().getLocation())) {
          event.getBlock().setType(Material.AIR);
          ItemStack computer = new ItemStack(ComputerUtils.getComputerMaterial());
          ItemMeta computerMeta = computer.getItemMeta();
          computerMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Computer"));
          computer.setItemMeta(computerMeta);
          if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.getPlayer().getInventory().addItem(new ItemStack[] { computer }); 
          event.setCancelled(true);
          computerfunctions.deleteComputer(event.getPlayer(), rec.id);
          return;
        } 
      }  
  }
}
