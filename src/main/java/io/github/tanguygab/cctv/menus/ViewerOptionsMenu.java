package io.github.tanguygab.cctv.menus;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class ViewerOptionsMenu extends CCTVMenu {

    private final ViewerManager vm = cctv.getViewers();

    public ViewerOptionsMenu(Player p) {
        super(p);
        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, lang.CAMERA_VIEW_OPTIONS_TITLE);
    }

    @Override
    public void open() {
        inv.setItem(0, vm.get(player).isNightVision() ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());

        if (vm.SPOTTING) inv.setItem(1, getItem(Heads.SPOTTING,lang.CAMERA_VIEW_OPTIONS_SPOT));

        if (vm.ZOOM_ITEM) {
            if (!cctv.getCameras().EXPERIMENTAL_VIEW) {
                PotionEffect effect = player.getPotionEffect(NMSUtils.SLOWNESS);
                inv.setItem(2, getItem(Heads.ZOOM,
                        effect != null
                                ? lang.getCameraViewZoom(effect.getAmplifier() + 1)
                                : lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF
                ));
            } else inv.setItem(2,getItem(Heads.ZOOM,lang.CAMERA_VIEW_OPTIONS_ZOOM_UNAVAILABLE));
        }

        inv.setItem(4, getItem(Heads.EXIT,lang.CAMERA_VIEW_OPTIONS_BACK));
        player.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> {
                Viewer v = vm.get(player);
                v.setNightVision(!v.isNightVision());
                inv.setItem(0, v.isNightVision() ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());
            }
            case 1 -> spotting(player);
            case 2 -> {
                if (!vm.ZOOM_ITEM || cctv.getCameras().EXPERIMENTAL_VIEW) return;
                PotionEffect effect = player.getPotionEffect(NMSUtils.SLOWNESS);
                if (effect == null) {
                    zoom(player, 1);
                    return;
                }
                int zoom = effect.getAmplifier()+1;
                zoom(player, zoom == 6 ? 0 : zoom+1);
            }
            case 4 -> player.closeInventory();
        }
    }

    private void spotting(Player p) {
        if (!vm.SPOTTING) return;
        if (!cctv.getNms().isNmsSupported()) {
            p.sendMessage(CCTV.getInstance().getLang().UNSUPPORTED);
            return;
        }
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
        if (zoomLevel == 0) {
            p.removePotionEffect(NMSUtils.SLOWNESS);
            inv.setItem(2, Heads.ZOOM.get());
            return;
        }
        p.addPotionEffect(new PotionEffect(NMSUtils.SLOWNESS, -1, zoomLevel - 1, false, false));
        inv.setItem(2, getItem(Heads.ZOOM,lang.getCameraViewZoom(zoomLevel)));
    }
}
