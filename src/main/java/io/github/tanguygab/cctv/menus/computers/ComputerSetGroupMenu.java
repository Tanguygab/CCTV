package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

        List<CameraGroup> groups = new ArrayList<>();
        for (String name : cgm.get(p))
            if (cgm.exists(name)) groups.add(cgm.get(name));

        for (int a = (page - 1) * 48; a < 48 * page && a < groups.size(); a++)
            inv.addItem(Utils.getItem(Heads.CAMERA, ChatColor.GOLD + "Group: " + ChatColor.YELLOW + groups.get(a).getId()));
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
