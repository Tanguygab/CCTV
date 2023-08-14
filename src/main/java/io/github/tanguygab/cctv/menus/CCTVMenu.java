package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public abstract class CCTVMenu {

    protected static final DecimalFormat posFormat = new DecimalFormat("#.##");

    protected final Player p;
    public Inventory inv;
    protected CCTV cctv = CCTV.getInstance();
    protected LanguageFile lang = cctv.getLang();
    public boolean renaming = false;
    private CCTVMenu previousMenu;

    protected CCTVMenu(Player p) {
        this.p = p;
    }

    public abstract void open();

    public abstract void onClick(ItemStack item, int slot, ClickType click);

    public void close() {
        if (renaming) {
            renaming = false;
            return;
        }
        Listener.openedMenus.remove(p);
    }

    public void back() {
        if (previousMenu != null) open(previousMenu);
        else p.closeInventory();
    }

    public void fillSlots(int... slots) {
        ItemStack filler = getItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (Integer slot : slots) inv.setItem(slot,filler);
    }

    public void open(CCTVMenu menu) {
        cctv.openMenu(p,menu);
        menu.previousMenu = this;
    }

    public static ItemStack getItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getItem(Material mat, String name) {
        return CCTVMenu.getItem(new ItemStack(mat),name);
    }
    public static ItemStack getItem(Heads head, String name) {
        return CCTVMenu.getItem(head.get(),name);
    }

}
