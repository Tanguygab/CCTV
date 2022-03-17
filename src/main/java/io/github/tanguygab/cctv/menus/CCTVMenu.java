package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class CCTVMenu {

    protected final Player p;
    public Inventory inv;
    protected CCTV cctv = CCTV.get();
    protected LanguageFile lang = cctv.getLang();
    public boolean renaming = false;

    protected CCTVMenu(Player p) {
        this.p = p;
    }

    public abstract void open();

    public abstract void onClick(ItemStack item, int slot);

    public void close() {
        if (renaming) {
            renaming = false;
            return;
        }
        Listener.openedMenus.remove(p);
    }

    public void fillSlots(int... slots) {
        ItemStack filler = Utils.getItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (Integer slot : slots) inv.setItem(slot,filler);
    }

    public void open(CCTVMenu menu) {
        cctv.openMenu(p,menu);
    }

}
