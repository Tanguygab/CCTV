package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.ListMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ComputerAddPlayersMenu extends ListMenu {

    private final Computer computer;

    protected ComputerAddPlayersMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerAddPlayer(page);
    }

    @Override
    protected void onOpen() {
        addClickableItem(18, getItem(Material.PLAYER_HEAD,lang.GUI_COMPUTER_OPTIONS_ADD_PLAYER));

        list(Arrays.stream(Bukkit.getServer().getOfflinePlayers())
                .filter(off->!computer.getAllowedPlayers().contains(off.getUniqueId().toString()))
                .toList(),off->{
            ItemStack item = getItem(Material.PLAYER_HEAD, off.getName());
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(off);
            setMeta(meta,off.getUniqueId().toString());
            item.setItemMeta(meta);
            inv.addItem(item);
        });
    }

    @Override
    protected void onClick(int slot) {
        Listener.computerAddPlayer.put(p,computer);
        p.closeInventory();
        p.sendMessage(lang.CHAT_PROVIDE_PLAYER,lang.CHAT_TYPE_CANCEL);
    }

    @Override
    protected void onClick(String uuid, ClickType click) {
        computer.addPlayer(uuid);
        open();
        p.sendMessage(lang.PLAYER_ADDED);
    }
}
