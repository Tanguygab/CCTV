package io.github.tanguygab.cctv.menus.cameras;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.CustomHeads;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class CameraSkinMenu extends CCTVMenu {


    private final Camera camera;
    private final CustomHeads heads;

    public CameraSkinMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
        heads = cctv.getCustomHeads();
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiCameraSkin(page+""));

        fillSlots(9,18);
        updateCameraItem();
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, getItem(Heads.EXIT,lang.GUI_CAMERA_EXIT));

        list(heads.getHeads(),skin-> inv.addItem(getItem(heads.get(skin),"&eSkin: "+skin.replace("_DEFAULT_","Default"))));
        p.openInventory(inv);
    }

    private void updateCameraItem() {
        inv.setItem(0, getItem(heads.get(camera.getSkin()),"&aCurrent Camera: "+camera.getSkin().replace("_DEFAULT_","Default")));
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> back();
            default -> {
                if (item == null || item.getType() == Material.AIR) return;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String itemName = ChatColor.stripColor(meta.getDisplayName());
                if (!itemName.startsWith("Skin: ")) return;
                String skin = itemName.substring(6).replace("Default","_DEFAULT_");
                camera.setSkin(skin);
                updateCameraItem();
            }
        }
    }
}
