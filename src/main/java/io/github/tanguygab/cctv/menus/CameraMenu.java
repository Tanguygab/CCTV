package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.utils.CustomHeads;
import io.github.tanguygab.cctv.utils.Heads;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CameraMenu extends CCTVMenu {

    private final Camera camera;
    private final CustomHeads heads;

    public CameraMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
        heads = cctv.getCustomHeads();
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 45, lang.getGuiCamera(camera.getId()));

        fillSlots(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,41,42,43);
        updateCameraItem();
        updateVisibilityItem();
        updateEnabledItem();
        inv.setItem(33,getItem(Material.NAME_TAG,"&aRename Camera"));
        inv.setItem(24,getItem(Material.ITEM_FRAME,"&aView"));

        inv.setItem(11,Heads.ROTATE_UP.get());
        inv.setItem(19,Heads.ROTATE_LEFT.get());
        inv.setItem(21,Heads.ROTATE_RIGHT.get());
        inv.setItem(29,Heads.ROTATE_DOWN.get());

        inv.setItem(40, getItem(Heads.EXIT,lang.GUI_CAMERA_EXIT));
        inv.setItem(44, getItem(Material.BARRIER,lang.GUI_CAMERA_DELETE));
        p.openInventory(inv);
    }

    private void updateCameraItem() {
        ItemStack item = heads.get(camera.getSkin());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        heads.heads.forEach((name,i)-> lore.add(ChatColor.translateAlternateColorCodes('&',
                "&8\u00BB "
                        +(name.equals(camera.getSkin()) ? "&6" : "&e")
                        +(name.equals("_DEFAULT_") ? "Default" : name)
        )));
        meta.setLore(lore);
        meta.setDisplayName(lang.GUI_CAMERA_CHANGE_SKIN);
        item.setItemMeta(meta);
        inv.setItem(15,item);
    }
    private void updateVisibilityItem() {
        inv.setItem(23,camera.isShown()
                ? getItem(Material.ENDER_EYE,"&aCamera Shown")
                : getItem(Material.ENDER_PEARL,"&cCamera Hidden"));
    }
    private void updateEnabledItem() {
        inv.setItem(25,camera.isEnabled()
                ? getItem(Material.REDSTONE_TORCH,"&aCamera Enabled")
                : getItem(Material.LEVER,"&cCamera Disabled"));
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        CameraManager cm = cctv.getCameras();
        switch (slot) {
            case 15 -> {
                camera.setSkin(heads.findNext(camera.getSkin(),click.isRightClick()));
                updateCameraItem();
            }
            case 23 -> {
                camera.setShown(!camera.isShown());
                updateVisibilityItem();
            }
            case 24 -> {
                p.closeInventory();
                cm.viewCamera(p, camera, null);
            }
            case 25 -> {
                camera.setEnabled(!camera.isEnabled());
                updateEnabledItem();
            }
            case 33 -> {
                Listener.cameraRename.put(p,camera);
                p.closeInventory();
                p.sendMessage(lang.CHAT_PROVIDE_NAME);
                p.sendMessage(lang.CHAT_TYPE_CANCEL);
            }

            case 11 -> cm.rotateVertically(p,camera, -9);
            case 19 -> cm.rotateHorizontally(p,camera, 18);
            case 21 -> cm.rotateHorizontally(p,camera, -18);
            case 29 -> cm.rotateVertically(p,camera, 9);

            case 40 -> back();
            case 44 -> {
                p.closeInventory();
                p.getInventory().addItem(heads.get(camera.getSkin()));
                cm.delete(camera.getId(), p);
            }
        }
    }
}
