package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ViewersEvents implements Listener {

    private final LanguageFile lang;
    private final CameraManager cm;
    private final ViewerManager vm;

    public ViewersEvents() {
        lang = CCTV.get().getLang();
        cm = CCTV.get().getCameras();
        vm = CCTV.get().getViewers();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        if (vm.exists(e.getPlayer()) && !CCTV.get().getConfiguration().getBoolean("allowed_to_chat",false))
            e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!vm.exists(p)) return;

        p.setAllowFlight(true);
        p.setFlying(true);
        cm.teleport(vm.get(p).getCamera(), p);
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!vm.exists(player)) return;

        player.sendTitle(" ", lang.CAMERA_DISCONNECTING, 0, 15, 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> cm.unviewCamera(player),  vm.TIME_TO_DISCONNECT * 20L);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        cm.unviewCamera(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryInteractEvent e) {
        if (vm.exists(e.getWhoClicked().getUniqueId().toString()))
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDropItemEvent e) {
        if (vm.exists(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (vm.exists(p)) e.setCancelled(true);
            return;
        }
        if (!(e.getDamager() instanceof Player p)) {
            if (e.getEntity() instanceof ArmorStand as && cm.values().stream().anyMatch(cam -> cam.getArmorStand() == as))
                e.setCancelled(true);
            return;
        }

        if (vm.exists(p)) {
            if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                ItemStack item = p.getInventory().getItemInMainHand();
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
                    vm.switchFunction(p, item.getItemMeta().getDisplayName());
            }
            e.setCancelled(true);
            return;
        }

        if (!(e.getEntity() instanceof ArmorStand as)) return;
        if (as.getCustomName() == null) return;
        String name = ChatColor.stripColor(as.getCustomName());
        if (!name.startsWith("CAM-") || !cm.exists(name)) return;

        if (cm.values().stream().noneMatch(cam -> cam.getArmorStand() == as)) {
            as.remove();
            p.sendMessage(lang.CAMERA_DELETED_BECAUSE_BUGGED);
            return;
        }

        List<String> cameras = cm.get(p);
        if (!cameras.contains(name)) return;

        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, lang.getGuiCameraDelete(as.getCustomName().substring(4)));
        inv.setItem(1, Utils.getItem(Material.RED_WOOL,lang.GUI_CAMERA_DELETE_ITEM_CANCEL));
        inv.setItem(3, Utils.getItem(Material.LIME_WOOL,lang.GUI_CAMERA_DELETE_ITEM_DELETE));
        p.openInventory(inv);
    }
}
