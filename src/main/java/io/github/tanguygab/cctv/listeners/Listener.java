package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Listener implements org.bukkit.event.Listener {

    private final LanguageFile lang;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public Listener() {
        lang = CCTV.get().getLang();
        cpm = CCTV.get().getComputers();
        vm = CCTV.get().getViewers();
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand as)) return;
        String customName = as.getCustomName();
        if (customName != null && ChatColor.stripColor(customName).startsWith("CAM-")) e.setCancelled(true);
        Player player = e.getPlayer();
        if (!vm.exists(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        Material mat = item.getType();

        if (mat != Material.PLAYER_HEAD && mat != Material.ENDER_PEARL && mat != Material.ENDER_EYE) return;

        viewfunctions.switchFunctions(player, item);
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        List<Player> chatInput = CCTV.get().chatInput;
        if (!chatInput.contains(player)) return;
        e.setCancelled(true);
        chatInput.remove(player);

        if (message.equals("exit")) {
            player.sendMessage(ChatColor.RED + "You have stopped adding players to the computer!");
            return;
        }
        OfflinePlayer off = Utils.getOfflinePlayer(message);
        if (off == null) {
            player.sendMessage(lang.PLAYER_NOT_FOUND);
            return;
        }
        Computer pc = cpm.get(computerfunctions.getLastClickedComputerFromPlayer(player));
        String uuid = off.getUniqueId().toString();
        if (pc.isAllowedPlayers(off) || pc.getOwner().equals(uuid)) {
            player.sendMessage(lang.PLAYER_ALREADY_ADDED);
            return;
        }
        pc.getAllowedPlayers().add(uuid);
        player.sendMessage(lang.PLAYER_ADDED);
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
