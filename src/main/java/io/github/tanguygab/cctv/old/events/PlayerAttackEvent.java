package io.github.tanguygab.cctv.old.events;

import java.util.ArrayList;
import java.util.List;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.utils.CameraUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerAttackEvent {
  public void onPlayerAttackEvent(EntityDamageByEntityEvent event) {
    ViewerManager vm = CCTV.get().getViewers();
    if (event.getDamager() instanceof Player damager) {
      if (vm.exists(damager)) {
        if (!damager.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
          ItemStack item = damager.getInventory().getItemInMainHand();
          viewfunctions.switchFunctions(damager, item);
        } 
        event.setCancelled(true);
      } 
    } 
    if (event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
      if (!(event.getDamager() instanceof Player damager))
        return;
      if (event.getEntity().getCustomName() == null)
        return;
      CameraManager cm = CCTV.get().getCameras();
      if (cm.values().stream().anyMatch(c -> c.getArmorStand().equals(event.getEntity()))) {
        event.setCancelled(true);
      } else if (event.getEntity().getCustomName() != null && ChatColor.stripColor(event.getEntity().getCustomName()).startsWith("CAM-")) {
        event.getEntity().remove();
        damager.sendMessage(Arguments.camera_deleted_because_bugged);
        return;
      }

      if (ChatColor.stripColor(event.getEntity().getCustomName()).startsWith("CAM-") && cm.exists(event.getEntity().getCustomName().substring(4))) {
        List<String> cameras = cm.getCamerasFromPlayer(damager);
        if (cameras.contains(event.getEntity().getCustomName().substring(4))) {
          if (vm.exists(damager))
            return; 
          Inventory inv = Bukkit.createInventory(null, 9, Arguments.gui_camera_delete.replaceAll("%CameraID%", event.getEntity().getCustomName().substring(4)));
          ItemStack green = new ItemStack(Material.LIME_WOOL);
          ItemStack red = new ItemStack(Material.RED_WOOL);
          ItemMeta greenm = green.getItemMeta();
          ItemMeta redm = red.getItemMeta();
          greenm.setDisplayName(Arguments.gui_camera_delete_item_delete);
          redm.setDisplayName(Arguments.gui_camera_delete_item_cancel);
          red.setItemMeta(redm);
          green.setItemMeta(greenm);
          inv.setItem(3, red);
          inv.setItem(5, green);
          damager.openInventory(inv);
        } 
      } 
    } 
    if (event.getEntity() instanceof Player player) {
      if (vm.exists(player))
        event.setCancelled(true); 
    } 
  }
}

