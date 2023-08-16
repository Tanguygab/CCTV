package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ListMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;

public class ComputerRemovePlayerMenu extends ListMenu {

    private final Computer computer;

    protected ComputerRemovePlayerMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerRemovePlayer(page);
    }

    @Override
    protected void onOpen() {
        list(computer.getAllowedPlayers(),uuid->{
            OfflinePlayer off = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
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
    protected void onClick(String uuid, ClickType click) {
        computer.removePlayer(uuid);
        open();
        player.sendMessage(lang.PLAYER_REMOVED);
    }

}
