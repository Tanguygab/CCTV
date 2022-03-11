package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.old.records.ChatRecord;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.utils.CameraUtils;
import io.github.tanguygab.cctv.utils.ComputerUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerInteractEvent e) {

    }



    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(ComputerUtils.getComputerMaterial())) return;
        Player p = e.getPlayer();
        Computer rec = computerfunctions.getComputerRecordFromLocation(e.getBlock().getLocation());
        if (rec == null) return;
        if (!rec.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.computer.other")) return;
        e.getBlock().setType(Material.AIR);

        if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(Utils.getComputer());
        e.setCancelled(true);
        computerfunctions.deleteComputer(p, rec.getId());


    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Computer"))
            computerfunctions.createComputer(e.getPlayer(), "", e.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent e) {
        io.github.tanguygab.cctv.listeners.InventoryClickEvent.on(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand as)) return;
        String customName = as.getCustomName();
        if (customName != null && ChatColor.stripColor(customName).startsWith("CAM-")) e.setCancelled(true);
        Player player = e.getPlayer();
        if (!CCTV.get().getViewers().exists(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        Material mat = item.getType();
        if (mat == Material.PLAYER_HEAD || mat == Material.ENDER_PEARL || mat == Material.ENDER_EYE) {
            viewfunctions.switchFunctions(player, item);
            e.setCancelled(true);
        }

    }


    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!CCTV.get().getViewers().exists(player)) return;

        player.sendTitle("", CCTV.get().getLang().CAMERA_DISCONNECTING, 0, 15, 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> CameraUtils.unviewCamera(player),  CCTV.get().TIME_TO_DISCONNECT * 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        LanguageFile lang = CCTV.get().getLang();

        Player player = e.getPlayer();
        String message = e.getMessage();

        ChatRecord.ChatRec chat = null;
        for (ChatRecord.ChatRec chat2 : ChatRecord.chats) {
            if (chat2.uuid.equals(player.getUniqueId().toString())) {
                if (chat2.chat) return;
                chat = chat2;
            }
        }
        if (chat == null) return;
        e.setCancelled(true);

        if (message.equals("exit")) {
            player.sendMessage(ChatColor.RED + "You have stopped adding players to the computer!");
            chat.chat = true;
            return;
        }
        for (OfflinePlayer off : Bukkit.getServer().getOfflinePlayers()) {
            if (off == null || off.getName() == null || !off.getName().equalsIgnoreCase(message)) continue;
            Computer pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
            String uuid = off.getUniqueId().toString();
            if (pc.isAllowedPlayers(off) || pc.getOwner().equals(uuid)) {
                player.sendMessage(lang.PLAYER_ALREADY_ADDED);
                chat.chat = true;
                return;
            }
            pc.getAllowedPlayers().add(uuid);
            player.sendMessage(lang.PLAYER_ADDED);
            chat.chat = true;
            return;
        }
        player.sendMessage(lang.PLAYER_NOT_FOUND);
        chat.chat = true;


    }


}
