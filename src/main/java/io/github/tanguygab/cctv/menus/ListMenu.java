package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ListMenu extends CCTVMenu {

    protected NamespacedKey itemKey = new NamespacedKey(cctv,"item");
    private int page = 1;
    private final List<Integer> clickableItems = new ArrayList<>();

    protected ListMenu(Player p) {
        super(p);
    }

    protected abstract String getTitle(int page);
    protected abstract void onOpen();
    protected void onClick(int slot) {}
    protected abstract void onClick(String name, ClickType click);

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, getTitle(page));

        fillSlots(0,9,18);
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

        onOpen();

        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> back();
            default -> {
                if (clickableItems.contains(slot)) {
                    onClick(slot);
                    return;
                }
                String name = getKey(item,itemKey);
                if (name == null) return;
                onClick(name,click);
            }
        }
    }

    protected void addClickableItem(int slot, ItemStack item) {
        clickableItems.add(slot);
        inv.setItem(slot,item);
    }

    protected void setPage(int page) {
        if (page < 1) return;
        this.page = page;
        renaming = true;
        open();
    }

    protected  <T> void list(List<T> list, Consumer<T> run) {
        for (int i = (page - 1) * 48; i < 48 * page && i < list.size(); i++) {
            run.accept(list.get(i));
        }
    }

    protected void setMeta(ItemMeta meta, String data) {
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING,data);
    }

    private String getKey(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return null;
        String text = meta.getPersistentDataContainer().get(key,PersistentDataType.STRING);
        return text == null ? null : text.replace(ChatColor.COLOR_CHAR,'&');
    }

}
