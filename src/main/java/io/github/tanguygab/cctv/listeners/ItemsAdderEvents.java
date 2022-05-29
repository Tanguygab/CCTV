package io.github.tanguygab.cctv.listeners;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderEvents implements Listener {

    private final CCTV cctv;
    private final ComputerManager cpm;
    private final String mat;

    public ItemsAdderEvents(ComputerManager cpm, String mat) {
        cctv = CCTV.get();
        this.cpm = cpm;
        this.mat = mat;
    }

    @EventHandler
    public void on(ItemsAdderLoadDataEvent e) {
        CustomStack stack = CustomStack.getInstance(mat);
        if (stack == null) {
            cctv.getLogger().info("Invalid ItemsAdder block as computer! Defaulting to Nether Brick Stairs...");
            cpm.COMPUTER_ITEM = CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME);
            return;
        }
        if (!stack.isBlock()) {
            cctv.getLogger().info("ItemsAdder item for computer is not a block! Defaulting to Nether Brick Stairs...");
            cpm.COMPUTER_ITEM = CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME);
            return;
        }
        cpm.COMPUTER_ITEM = stack.getItemStack();
        cctv.getLogger().info("ItemsAdder item "+stack.getNamespace()+" loaded!");
    }

    @EventHandler
    public void on(CustomBlockPlaceEvent e) {
        if (e.getCustomBlockItem().isSimilar(cpm.COMPUTER_ITEM))
            cpm.create(null,e.getPlayer(), e.getBlock().getLocation());;
    }
}