package io.github.tanguygab.cctv.listeners;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;

public class ItemsAdderEvents extends Listener {

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
            ComputerManager.COMPUTER_ITEM = CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME);
            return;
        }
        if (!stack.isBlock()) {
            cctv.getLogger().info("ItemsAdder item for computer is not a block! Defaulting to Nether Brick Stairs...");
            ComputerManager.COMPUTER_ITEM = CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME);
            return;
        }
        ComputerManager.COMPUTER_ITEM = stack.getItemStack();
        cctv.getLogger().info("ItemsAdder item "+stack.getNamespace()+" loaded!");
    }

    @EventHandler
    public void on(CustomBlockPlaceEvent e) {
        if (e.getCustomBlockItem().isSimilar(ComputerManager.COMPUTER_ITEM))
            cpm.create(null,e.getPlayer(), e.getBlock().getLocation());;
    }

    @EventHandler
    public void on(CustomBlockInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!e.getCustomBlockItem().isSimilar(ComputerManager.COMPUTER_ITEM)) return;
        Computer computer = cpm.get(e.getBlockClicked().getLocation());
        if (computer == null) return;
        Player p = e.getPlayer();
        if (computer.canUse(p)) cpm.open(p, computer);
        else p.sendMessage(CCTV.get().getLang().COMPUTER_NOT_ALLOWED);
        e.setCancelled(true);
    }

    @EventHandler
    public void on(CustomBlockBreakEvent e) {
        if (!e.getCustomBlockItem().isSimilar(ComputerManager.COMPUTER_ITEM)) return;
        Computer computer = cpm.get(e.getBlock().getLocation());
        if (computer == null) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        if (!computer.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.computer.other")) return;
        e.getBlock().setType(Material.AIR);

        if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(ComputerManager.COMPUTER_ITEM.clone());
        cpm.delete(computer.getId(),p);
    }
}