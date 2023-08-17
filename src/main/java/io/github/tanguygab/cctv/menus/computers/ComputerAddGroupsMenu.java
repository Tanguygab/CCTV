package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.menus.ListMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ComputerAddGroupsMenu extends ListMenu {

    private final Computer computer;
    private final CameraGroupManager cgm = cctv.getGroups();

    protected ComputerAddGroupsMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerAddGroup(page);
    }

    @Override
    protected void onOpen() {
        list(cgm.get(player).stream()
                .map(cgm::get)
                .filter(group->!computer.getCameras().contains(group))
                .toList(),group->{
            ItemStack item = getItem(group.getIcon(),group.getName());
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            setMeta(meta,group.getName());
            item.setItemMeta(meta);
            inv.addItem(item);
        });
    }

    @Override
    protected void onClick(String name, ClickType click) {
        computer.addCamera(cgm.get(name));
        open();
        player.sendMessage(lang.getEditCameras(true,true,false,true));
    }
}
