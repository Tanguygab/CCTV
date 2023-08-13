package io.github.tanguygab.cctv.listeners;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@AllArgsConstructor
public class ItemsAdderEvents implements Listener {

    private final CCTV cctv = CCTV.getInstance();
    private final ComputerManager cpm;
    private final String mat;
    private final boolean admin;

    @EventHandler
    public void on(ItemsAdderLoadDataEvent e) {
        CustomStack stack = CustomStack.getInstance(mat);
        if (stack == null) {
            cctv.getLogger().warning("Invalid ItemsAdder block as computer! Defaulting to Nether Brick Stairs...");
            setItem(CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME));
            return;
        }
        if (!stack.isBlock()) {
            cctv.getLogger().warning("ItemsAdder item for computer is not a block! Defaulting to Nether Brick Stairs...");
            setItem(CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, cctv.getLang().COMPUTER_ITEM_NAME));
            return;
        }
        setItem(stack.getItemStack());
        cctv.getLogger().info("ItemsAdder item "+stack.getNamespace()+" loaded!");
    }
    private void setItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(cpm.computerKey, PersistentDataType.STRING,admin ? "admin" : "normal");
        item.setItemMeta(meta);
        cpm.COMPUTER_ITEM = item;
    }
}