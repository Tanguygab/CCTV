package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.utils.Heads;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CameraMenu extends CCTVMenu {

    private final Camera camera;

    public CameraMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.getGuiCamera(camera.getId()));

        inv.setItem(0,getCameraItem());
        inv.setItem(2, getItem(Material.BARRIER,lang.GUI_CAMERA_DELETE));
        inv.setItem(4, getItem(Heads.EXIT,lang.GUI_CAMERA_EXIT));
        p.openInventory(inv);
    }

    private ItemStack getCameraItem() {
        ItemStack item = cctv.getCustomHeads().get(camera.getSkin());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        cctv.getCustomHeads().heads.forEach((name,i)-> lore.add(ChatColor.translateAlternateColorCodes('&',
                "&8\u00BB "
                        +(name.equals(camera.getSkin()) ? "&6" : "&e")
                        +(name.equals("_DEFAULT_") ? "Default" : name)
        )));
        meta.setLore(lore);
        meta.setDisplayName(lang.GUI_CAMERA_CHANGE_SKIN);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> {
                camera.setSkin(cctv.getCustomHeads().findNext(camera.getSkin(),click.isRightClick()));
                inv.setItem(0,getCameraItem());
            }
            case 2 -> {
                p.closeInventory();
                p.getInventory().addItem(cctv.getCustomHeads().get(camera.getSkin()));
                cctv.getCameras().delete(camera.getId(), p);
            }
            case 4 -> back();
        }
    }
}
