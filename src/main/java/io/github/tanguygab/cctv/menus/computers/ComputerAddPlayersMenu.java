package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ComputerAddPlayersMenu extends ComputerMenu {

    protected ComputerAddPlayersMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerAddPlayer(String.valueOf(page)));

        fillSlots(0,9);
        inv.setItem(18, getItem(Material.PLAYER_HEAD,lang.GUI_COMPUTER_OPTIONS_ADD_PLAYER));
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

        List<OfflinePlayer> list = Arrays.stream(Bukkit.getServer().getOfflinePlayers()).filter(off->!computer.getAllowedPlayers().contains(off.getUniqueId().toString())).toList();

        list(list,off->{
            ItemStack item = getItem(Material.PLAYER_HEAD, ChatColor.YELLOW + "Player: " + off.getName());
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(off);
            item.setItemMeta(meta);
            inv.addItem(item);
        });

        p.openInventory(inv);
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 18 -> {
                Listener.computerAddPlayer.put(p,computer);
                p.closeInventory();
                p.sendMessage(lang.CHAT_PROVIDE_PLAYER);
                p.sendMessage(lang.CHAT_TYPE_CANCEL);
            }
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> back();
            default -> {
                String player = getItemName(item,"Player: ");
                if (player == null) return;
                OfflinePlayer off = Utils.getOfflinePlayer(player);
                if (off == null) return;
                computer.addPlayer(off.getUniqueId().toString());
                setPage(page);
                p.sendMessage(lang.PLAYER_ADDED);
            }
        }
    }
}
