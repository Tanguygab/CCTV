package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.CameraMenu;
import io.github.tanguygab.cctv.utils.NMSUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    private final LanguageFile lang;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public static final List<Player> chatInput = new ArrayList<>();
    public static final Map<Player,Computer> lastClickedComputer = new HashMap<>();
    public static Map<Player,CCTVMenu> openedMenus = new HashMap<>();

    public Listener() {
        lang = CCTV.get().getLang();
        cpm = CCTV.get().getComputers();
        vm = CCTV.get().getViewers();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent e) {
        InteractEvent.on(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(ComputerManager.COMPUTER_MATERIAL)) return;
        Player p = e.getPlayer();
        Computer rec = cpm.get(e.getBlock().getLocation());
        if (rec == null) return;
        if (!rec.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.computer.other")) return;
        e.getBlock().setType(Material.AIR);

        if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(Utils.getComputer());
        e.setCancelled(true);
        cpm.delete(rec.getId(),p);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Computer"))
            cpm.create(null,e.getPlayer(), e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand as)) return;
        String customName = as.getCustomName();
        if (customName == null || !ChatColor.stripColor(customName).startsWith("CAM-")) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        CameraManager cm = CCTV.get().getCameras();

        Camera camera = null;
        for (Camera cam : cm.values()) {
            if (cam.getArmorStand().getUniqueId().equals(as.getUniqueId())) {
                if (cam.getArmorStand() != as) cam.setArmorStand(as);
                camera = cam;
            }
        }

        if (camera == null) {
            as.remove();
            p.sendMessage(lang.CAMERA_DELETED_BECAUSE_BUGGED);
            return;
        }

        if (!cm.get(p).contains(camera.getId())) return;
        CCTV.get().openMenu(p,new CameraMenu(p,camera));
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if (!chatInput.contains(p)) return;
        e.setCancelled(true);
        chatInput.remove(p);

        if (msg.equals("exit")) {
            p.sendMessage(ChatColor.RED + "You have stopped adding players to the computer!");
            return;
        }
        OfflinePlayer off = Utils.getOfflinePlayer(msg);
        if (off == null) {
            p.sendMessage(lang.PLAYER_NOT_FOUND);
            return;
        }
        Computer computer = lastClickedComputer.get(p);
        if (computer == null) {
            p.sendMessage(lang.COMPUTER_NOT_FOUND);
            return;
        }
        String uuid = off.getUniqueId().toString();
        if (computer.canUse(off)) {
            p.sendMessage(lang.PLAYER_ALREADY_ADDED);
            return;
        }
        computer.addPlayer(uuid);
        p.sendMessage(lang.PLAYER_ADDED);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player joined = event.getPlayer();
        joined.discoverRecipe(Utils.cameraKey);
        joined.discoverRecipe(Utils.computerKey);

        vm.values().forEach(player->{
            Player p = Bukkit.getServer().getPlayer(UUID.fromString(player.getId()));
            joined.hidePlayer(CCTV.get(),p);
            NMSUtils.spawnNPCForTarget(joined,p);
        });
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (openedMenus.containsKey(p)) {
            openedMenus.get(p).onClick(e.getCurrentItem(),e.getRawSlot());
            e.setCancelled(true);
            return;
        }
        if (vm.exists(p)) vm.onCameraItems(p, e.getCurrentItem());
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        CCTVMenu menu = openedMenus.get(p);
        if (menu != null && menu.inv.equals(e.getInventory())) openedMenus.get(p).close();
    }

}
