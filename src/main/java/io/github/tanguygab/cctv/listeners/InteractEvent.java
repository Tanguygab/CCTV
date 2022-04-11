package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
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

        if (CCTV.get().getViewers().exists(p)) {
            e.setCancelled(true);
            if (e.getHand() != EquipmentSlot.OFF_HAND)
                CCTV.get().getViewers().onCameraItems(p, item);
            return;
        }

        Block block = e.getClickedBlock();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || block == null) return;
        Location loc = block.getLocation();

        if (block.getType() == ComputerManager.COMPUTER_MATERIAL && e.getHand() != EquipmentSlot.OFF_HAND) {
            ComputerManager cpm = CCTV.get().getComputers();
            Computer computer = cpm.get(block.getLocation());
            if (computer != null) {
                if (computer.canUse(p)) cpm.open(p, computer);
                else p.sendMessage(CCTV.get().getLang().COMPUTER_NOT_ALLOWED);
                e.setCancelled(true);
                return;
            }
        }
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && item.getType() == Material.PLAYER_HEAD && CCTV.get().getCustomHeads().isCamera(item)) {
                createCamera(p, item, loc, e.getBlockFace());
                e.setCancelled(true);

            }
        }
    }

    private static void createCamera(Player p, ItemStack item, Location loc, BlockFace face) {
        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
            item.setAmount(item.getAmount()-1);
        switch (face) {
            case UP -> setLoc(loc,0.5D,0.5D,0.47D,loc.getYaw()+180.0F);
            case DOWN -> setLoc(loc,0.5D,0.5D,2.03D,loc.getYaw()+180.0F);
            case EAST -> setLoc(loc,1.29D,0.5D,1.24D,270.0F);
            case WEST -> setLoc(loc,-0.29D,0.5D,1.24D,90.0F);
            case NORTH -> setLoc(loc,0.5D,-0.29D,1.24D,180.0F);
            case SOUTH -> setLoc(loc,0.5D,1.29D,1.24D,0.0F);
        }
        CCTV.get().getCameras().create(null, loc, p,CCTV.get().getCustomHeads().get(item));
    }

    private static void setLoc(Location loc, double x, double z, double y, float yaw) {
        loc.setX(loc.getX()+x);
        loc.setZ(loc.getZ()+z);
        loc.setY(loc.getY()-y);
        loc.setYaw(yaw);
    }

}
