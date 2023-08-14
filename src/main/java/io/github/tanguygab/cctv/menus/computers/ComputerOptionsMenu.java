package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerOptionsMenu extends CCTVMenu {

    protected final Computer computer;

    public ComputerOptionsMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.GUI_COMPUTER_OPTIONS_TITLE);
    }

    @Override
    public void open() {
        inv.setItem(0, Heads.COMPUTER_BACK.get());
        inv.setItem(3, getItem(Heads.CAMERA.get(),lang.GUI_COMPUTER_OPTIONS_ADD_CAMERAS));
        setPublicItem();

        p.openInventory(inv);
    }

    private void setPublicItem() {
        ItemStack item = getItem(computer.isPublik() ? Material.CHEST : Material.ENDER_CHEST,lang.GUI_COMPUTER_OPTIONS_ACCESS_ITEM_NAME);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(List.of("",
                lang.getGuiComputerOptionsAccessItemStatus(computer.isPublik()),
                "",
                lang.GUI_COMPUTER_OPTIONS_ACCESS_ITEM_ADD_PLAYERS,
                lang.GUI_COMPUTER_OPTIONS_ACCESS_ITEM_REMOVE_PLAYERS,
                lang.GUI_COMPUTER_OPTIONS_ACCESS_ITEM_TOGGLE_ACCESS
        ));
        item.setItemMeta(meta);
        inv.setItem(4, item);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> open(new ComputerMainMenu(p,computer));
            case 3 -> open(new ComputerAddCamerasMenu(p,computer));
            case 4 -> {
                switch (click) {
                    case LEFT -> open(new ComputerAddPlayersMenu(p,computer));
                    case RIGHT -> open(new ComputerRemovePlayerMenu(p,computer));
                    case SHIFT_LEFT -> {
                        computer.setPublik(!computer.isPublik());
                        setPublicItem();
                    }
                }
            }
        }
    }
}
