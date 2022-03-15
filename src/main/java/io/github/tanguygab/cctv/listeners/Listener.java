package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.utils.NPCUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    private final LanguageFile lang;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public static final List<Player> chatInput = new ArrayList<>();
    public static final Map<Player,Computer> lastClickedComputer = new HashMap<>();

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
        if (customName == null || !(customName = ChatColor.stripColor(customName)).startsWith("CAM-")) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        CameraManager cm = CCTV.get().getCameras();
        if (cm.values().stream().noneMatch(cam -> cam.getArmorStand() == as)) {
            as.remove();
            p.sendMessage(lang.CAMERA_DELETED_BECAUSE_BUGGED);
            return;
        }

        List<String> cameras = cm.get(p);
        if (!cameras.contains(customName.substring(4))) return;

        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, lang.getGuiCameraDelete(as.getCustomName().substring(4)));
        inv.setItem(1, Utils.getItem(Material.RED_WOOL,lang.GUI_CAMERA_DELETE_ITEM_CANCEL));
        inv.setItem(3, Utils.getItem(Material.LIME_WOOL,lang.GUI_CAMERA_DELETE_ITEM_DELETE));
        p.openInventory(inv);
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
        computer.getAllowedPlayers().add(uuid);
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
            NPCUtils.spawnForTarget(joined,p);
        });
    }

}
