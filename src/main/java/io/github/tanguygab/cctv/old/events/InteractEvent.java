package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractEvent {
  public void onInteractEvent(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action act = event.getAction();
    if (act.equals(Action.RIGHT_CLICK_BLOCK) && 
      event.getItem() != null && event.getItem().getType().equals(Material.getMaterial("SKULL_ITEM")) && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equals(Arguments.camera_item_name)) {
      if (player.getGameMode() == GameMode.SURVIVAL)
        event.getItem().setAmount(event.getItem().getAmount() - 1); 
      Location loc = event.getClickedBlock().getLocation();
      String str;
      switch ((str = event.getBlockFace().toString().toLowerCase()).hashCode()) {
        case 3739 -> {
          if (!str.equals("up"))
            break;
          loc.setX(loc.getX() + 0.5D);
          loc.setZ(loc.getZ() + 0.5D);
          loc.setY(loc.getY() - 0.47D);
          loc.setYaw(player.getLocation().getYaw() + 180.0F);
        }
        case 3089570 -> {
          if (!str.equals("down"))
            break;
          loc.setX(loc.getX() + 0.5D);
          loc.setZ(loc.getZ() + 0.5D);
          loc.setY(loc.getY() - 2.03D);
          loc.setYaw(player.getLocation().getYaw() + 180.0F);
        }
        case 3105789 -> {
          if (!str.equals("east"))
            break;
          loc.setX(loc.getX() + 1.29D);
          loc.setZ(loc.getZ() + 0.5D);
          loc.setY(loc.getY() - 1.24D);
          loc.setYaw(270.0F);
        }
        case 3645871 -> {
          if (!str.equals("west"))
            break;
          loc.setX(loc.getX() - 0.29D);
          loc.setZ(loc.getZ() + 0.5D);
          loc.setY(loc.getY() - 1.24D);
          loc.setYaw(90.0F);
        }
        case 105007365 -> {
          if (!str.equals("north"))
            break;
          loc.setX(loc.getX() + 0.5D);
          loc.setZ(loc.getZ() - 0.29D);
          loc.setY(loc.getY() - 1.24D);
          loc.setYaw(180.0F);
        }
        case 109627853 -> {
          if (!str.equals("south"))
            break;
          loc.setX(loc.getX() + 0.5D);
          loc.setZ(loc.getZ() + 1.29D);
          loc.setY(loc.getY() - 1.24D);
          loc.setYaw(0.0F);
        }
      }
      CCTV.get().getCameras().create(null, loc, player);
      event.setCancelled(true);
      return;
    } 
    if ((act.equals(Action.RIGHT_CLICK_BLOCK) || act.equals(Action.RIGHT_CLICK_AIR)) && CCTV.get().getViewers().exists(player)) {
      event.setCancelled(true);
      if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR) && event.getHand() != EquipmentSlot.OFF_HAND) {
        ItemStack item = event.getItem();
        viewfunctions.switchFunctions(player, item);
      } 
      return;
    } 
    if (act.equals(Action.RIGHT_CLICK_BLOCK)) {
      if (!event.getClickedBlock().getType().equals(ComputerManager.COMPUTER_MATERIAL))
        return; 
      if (event.getHand().equals(EquipmentSlot.OFF_HAND))
        return; 
      Computer rec = computerfunctions.getComputerRecordFromLocation(event.getClickedBlock().getLocation());
      if (rec != null)
        if (computerfunctions.canPlayerAccessComputer(player, rec.getId())) {
          computerfunctions.setLastClickedComputerForPlayer(player, event.getClickedBlock().getLocation());
          camerafunctions.getCCTVFromComputer(player, event.getClickedBlock().getLocation());
        } else {
          player.sendMessage(Arguments.computer_not_allowed);
        }  
    } 
  }
}
