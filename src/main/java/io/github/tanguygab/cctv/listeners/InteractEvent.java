package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractEvent {

    public static void on(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        Block block = e.getClickedBlock();
        Location loc = block == null ? null : block.getLocation();
        boolean rcb = e.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (rcb && block != null) {
            if (!block.getType().equals(ComputerManager.COMPUTER_MATERIAL) || e.getHand() == EquipmentSlot.OFF_HAND) return;
            Computer computer = CCTV.get().getComputers().get(block.getLocation());
            if (computer != null) {
                if (computerfunctions.canPlayerAccessComputer(p, computer.getId())) {
                    computerfunctions.setLastClickedComputerForPlayer(p, loc);
                    camerafunctions.getCCTVFromComputer(p, loc);
                } else p.sendMessage(CCTV.get().getLang().COMPUTER_NOT_ALLOWED);
                return;
            }
        }

        if (item == null) return;
        ItemMeta meta = item.getItemMeta();

        if (rcb && item.getType() == Material.PLAYER_HEAD && meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(Arguments.camera_item_name)) {
            createCamera(p,item,loc,e.getBlockFace());
            e.setCancelled(true);
            return;
        }
        if ((rcb || e.getAction() == Action.RIGHT_CLICK_AIR) && CCTV.get().getViewers().exists(p)) {
            e.setCancelled(true);
            if (item.getType() != Material.AIR && e.getHand() != EquipmentSlot.OFF_HAND)
                viewfunctions.switchFunctions(p, item);
        }
    }

    private static void createCamera(Player p, ItemStack item, Location loc, BlockFace face) {
        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
            item.setAmount(item.getAmount() - 1);
        switch (face) {
            case UP -> {
                loc.setX(loc.getX() + 0.5D);
                loc.setZ(loc.getZ() + 0.5D);
                loc.setY(loc.getY() - 0.47D);
                loc.setYaw(p.getLocation().getYaw() + 180.0F);
            }
            case DOWN -> {
                loc.setX(loc.getX() + 0.5D);
                loc.setZ(loc.getZ() + 0.5D);
                loc.setY(loc.getY() - 2.03D);
                loc.setYaw(p.getLocation().getYaw() + 180.0F);
            }
            case EAST -> {
                loc.setX(loc.getX() + 1.29D);
                loc.setZ(loc.getZ() + 0.5D);
                loc.setY(loc.getY() - 1.24D);
                loc.setYaw(270.0F);
            }
            case WEST -> {
                loc.setX(loc.getX() - 0.29D);
                loc.setZ(loc.getZ() + 0.5D);
                loc.setY(loc.getY() - 1.24D);
                loc.setYaw(90.0F);
            }
            case NORTH -> {
                loc.setX(loc.getX() + 0.5D);
                loc.setZ(loc.getZ() - 0.29D);
                loc.setY(loc.getY() - 1.24D);
                loc.setYaw(180.0F);
            }
            case SOUTH -> {
                loc.setX(loc.getX() + 0.5D);
                loc.setZ(loc.getZ() + 1.29D);
                loc.setY(loc.getY() - 1.24D);
                loc.setYaw(0.0F);
            }
        }
        CCTV.get().getCameras().create(null, loc, p);
    }

}
