package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerMainMenu extends ComputerMenu {

    public ComputerMainMenu(Player p, Computer computer) {
        super(p,computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerDefault(page+""));

        fillSlots(9,18);
        inv.setItem(0, Utils.getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Utils.getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));
        setPage(page);

        p.openInventory(inv);
    }

    @Override
    protected void setPage(int page) {
        if (page < 1) return;
        this.page = page;
        CameraGroup group = computer.getCameraGroup();
        List<Integer> siderBarSlots = List.of(9,18,27,36,45);
        for (int i=1; i < 48; i++)
            if (!siderBarSlots.contains(i))
                inv.clear(i);

        if (group == null) return;

        for (int a = (page - 1) * 48; a < 48 * page && a < group.getCameras().size(); a++)
            inv.addItem(Utils.getItem(Heads.CAMERA, "&eCamera: " + group.getCameras().get(a).getId()));
    }

    @Override
    public void onClick(ItemStack item, int slot) {
        switch (slot) {
            case 0 -> {
                if (computer.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.computer.other"))
                    open(new ComputerOptionsMenu(p,computer));
                else p.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
            }
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> p.closeInventory();
            default -> {
                if (item == null || item.getType() == Material.AIR) return;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) return;
                String itemName = ChatColor.stripColor(meta.getDisplayName());
                if (!itemName.startsWith("Camera: ")) return;
                String camera = itemName.substring(8);
                cctv.getCameras().viewCamera(p, camera, computer.getCameraGroup());
                p.closeInventory();
            }
        }
    }
}
