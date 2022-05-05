package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.cameras.CameraMenu;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    private final CCTV cctv;
    private final LanguageFile lang;
    private final CameraManager cm;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public static final Map<Player,Camera> cameraRename = new HashMap<>();
    public static Map<Player,CCTVMenu> openedMenus = new HashMap<>();

    public Listener() {
        cctv = CCTV.get();
        lang = cctv.getLang();
        cm = cctv.getCameras();
        cpm = cctv.getComputers();
        vm = cctv.getViewers();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent e) {
        InteractEvent.on(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Computer computer = cpm.get(e.getBlock());
        if (computer == null) return;
        e.setCancelled(true);
        if (!computer.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.computer.other")) return;
        e.getBlock().setType(Material.AIR);

        if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(ComputerManager.COMPUTER_ITEM.clone());
        cpm.delete(computer.getId(),p);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Computer"))
            cpm.create(null,e.getPlayer(), e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (!(entity instanceof ArmorStand) && !(entity instanceof Creeper)) return;
        String customName = entity.getCustomName();
        if (customName == null || !ChatColor.stripColor(customName).startsWith("CAM-")) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        if (vm.exists(p)) {
            if (e.getHand() == EquipmentSlot.HAND && entity instanceof ArmorStand)
                vm.onCameraItems(p, p.getInventory().getItemInMainHand());
            e.setCancelled(true);
            return;
        }

        Camera camera = null;
        for (Camera cam : cm.values()) {
            if (cam.is(entity))
                camera = cam;
        }

        if (camera == null) {
            entity.remove();
            p.sendMessage(lang.CAMERA_DELETED_BECAUSE_BUGGED);
            return;
        }

        if (!cm.get(p).contains(camera.getId())) return;
        cctv.openMenu(p,new CameraMenu(p,camera));
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if (!cameraRename.containsKey(p)) return;
        e.setCancelled(true);
        Camera camera = cameraRename.get(p);
        cameraRename.remove(p);

        if (msg.equals("cancel")) {
            p.sendMessage(ChatColor.RED + "Camera renaming cancelled!");
            return;
        }

        if (msg.equals("")) {
            p.sendMessage(ChatColor.RED + "Please specify a new name!");
            return;
        }
        String newName = msg.split(" ")[0];
        if (camera.rename(newName)) {
            p.sendMessage(lang.getCameraRenamed(newName));
            Bukkit.getServer().getScheduler().runTask(cctv,()->cctv.openMenu(p,new CameraMenu(p,camera)));
            return;
        }
        p.sendMessage(lang.CAMERA_ALREADY_EXISTS);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.discoverRecipe(Utils.cameraKey);
        p.discoverRecipe(Utils.computerKey);
        if (cm.OLD_VIEW)
            for (Viewer viewer : vm.values()) p.hidePlayer(cctv,vm.get(viewer));
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (openedMenus.containsKey(p)) {
            openedMenus.get(p).onClick(e.getCurrentItem(),e.getRawSlot(),e.getClick());
            e.setCancelled(true);
            return;
        }
        if (vm.exists(p)) {
            e.setCancelled(true);
            vm.onCameraItems(p, e.getCurrentItem());
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        CCTVMenu menu = openedMenus.get(p);
        if (menu != null && menu.inv.equals(e.getInventory())) openedMenus.get(p).close();
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        if (vm.exists(e.getPlayer()))
            e.setCancelled(true);
    }

}
