package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class ComputerRemovePlayerMenu extends ComputerMenu {

    protected ComputerRemovePlayerMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerRemovePlayer(page+""));

        fillSlots(0,9,18);
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());
        setPage(page);

        p.openInventory(inv);
    }

    @Override
    protected void setPage(int page) {
        if (page < 1) return;
        this.page = page;
        List<Integer> siderBarSlots = List.of(9,18,27,36,45);
        for (int i=1; i < 48; i++)
            if (!siderBarSlots.contains(i))
                inv.clear(i);

        for (int a = (page - 1) * 48; a < 48 * page && a < computer.getAllowedPlayers().size(); a++) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(computer.getAllowedPlayers().get(a)));
            ItemStack item = Utils.getItem(Material.PLAYER_HEAD, ChatColor.YELLOW + "Player: " + off.getName());
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwningPlayer(off);
            item.setItemMeta(meta);
            inv.addItem(item);
        }
    }

    @Override
    public void onClick(ItemStack item, int slot) {
        switch (slot) {
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> open(new ComputerOptionsMenu(p,computer));
            default -> {
                if (item == null || item.getType() == Material.AIR) return;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String itemName = ChatColor.stripColor(meta.getDisplayName());
                if (!itemName.startsWith("Player: ")) return;
                String player = itemName.substring(8);
                OfflinePlayer off = Utils.getOfflinePlayer(player);
                computer.getAllowedPlayers().remove(off.getUniqueId().toString());
                setPage(page);
                p.sendMessage(lang.PLAYER_REMOVED);
            }
        }
    }
}
