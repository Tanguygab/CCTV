package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ComputersEvents implements Listener {

    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent e) {
        if (onMove(e.getBlocks())) e.setCancelled(true);
    }
    @EventHandler
    public void onPistonMove(BlockPistonRetractEvent e) {
        if (onMove(e.getBlocks())) e.setCancelled(true);
    }
    private boolean onMove(List<Block> blocks) {
        return blocks.stream().anyMatch(block -> CCTV.getInstance().getComputers().exists(block));
    }

    @EventHandler
    public void onBlockExplosionEvent(BlockExplodeEvent e) {
        onExplosion(e.blockList());
    }
    @EventHandler
    public void onEntityExplosionEvent(EntityExplodeEvent e) {
        onExplosion(e.blockList());
    }
    private void onExplosion(List<Block> blocks) {
        blocks.removeIf(block -> CCTV.getInstance().getComputers().exists(block));
    }

}
