package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ViewerOptionsMenu extends CCTVMenu {

    private final ViewerManager vm = cctv.getViewers();

    public ViewerOptionsMenu(Player p) {
        super(p);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.CAMERA_VIEW_OPTIONS_TITLE);
        inv.setItem(0, vm.get(p).isNightVision() ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());

        inv.setItem(1, getItem(Heads.SPOTTING,lang.CAMERA_VIEW_OPTIONS_SPOT));

        if (vm.ZOOM_ITEM) {
            if (!cctv.getCameras().EXPERIMENTAL_VIEW) {
                PotionEffect effect = p.getPotionEffect(PotionEffectType.SLOW);
                inv.setItem(2, getItem(Heads.ZOOM,
                        effect != null
                                ? lang.getCameraViewZoom(effect.getAmplifier() + 1)
                                : lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF
                ));
            } else inv.setItem(2,getItem(Heads.ZOOM,lang.CAMERA_VIEW_OPTIONS_ZOOM_UNAVAILABLE));
        }

        inv.setItem(4, getItem(Heads.EXIT,lang.CAMERA_VIEW_OPTIONS_BACK));
        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> {
                Inventory inv = p.getOpenInventory().getTopInventory();
                Viewer v = vm.get(p);
                v.setNightVision(!v.isNightVision());
                inv.setItem(0, v.isNightVision() ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());
            }
            case 1 -> spotting(p);
            case 2 -> {
                if (!vm.ZOOM_ITEM || cctv.getCameras().EXPERIMENTAL_VIEW) return;
                PotionEffect effect = p.getPotionEffect(PotionEffectType.SLOW);
                if (effect == null) {
                    zoom(p, 1);
                    return;
                }
                int zoom = effect.getAmplifier()+1;
                zoom(p, zoom == 6 ? 0 : zoom+1);
            }
            case 4 -> p.closeInventory();
        }
    }

    private void spotting(Player p) {
        p.closeInventory();
        List<Player> spotted = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().forEach(viewed->{
            if (spot(p, viewed,true))
                spotted.add(viewed);
        });
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(cctv, () -> spotted.forEach(viewed->spot(p,viewed,false)), vm.TIME_FOR_SPOT * 20L);
    }
    private boolean spot(Player viewer, Player viewed, boolean glow) {
        if (viewer == viewed || !viewer.canSee(viewed)) return false;
        cctv.getNms().glow(viewer,viewed,glow);
        return true;
    }
    private void zoom(Player p, int zoomLevel) {
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (zoomLevel == 0) {
            p.removePotionEffect(PotionEffectType.SLOW);
            inv.setItem(2, Heads.ZOOM.get());
            return;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, zoomLevel - 1, false, false));
        inv.setItem(2, getItem(Heads.ZOOM,lang.getCameraViewZoom(zoomLevel)));
    }
}
