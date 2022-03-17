package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerSetGroupMenu extends ComputerMenu {

    private final CameraGroupManager cgm = cctv.getCameraGroups();

    public ComputerSetGroupMenu(Player p, Computer computer) {
        super(p, computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerSetGroup(page+""));

        fillSlots(0,9,18);
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

        List<String> groups = cgm.get(p);
        for (int i = (page - 1) * 48; i < 48 * page && i < groups.size(); i++)
            inv.addItem(getItem(Heads.CAMERA, ChatColor.GOLD + "Group: " + ChatColor.YELLOW + groups.get(i)));

        p.openInventory(inv);
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
                if (!itemName.startsWith("Group: ")) return;
                String group = itemName.substring(7);
                CameraGroup camGroup = cgm.get(group);
                computer.setCameraGroup(camGroup);
                open(new ComputerOptionsMenu(p,computer));
                p.sendMessage(lang.GROUP_ASSIGNED_TO_COMPUTER);
            }
        }
    }
}
