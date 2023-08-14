package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ListMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerAddCamerasMenu extends ListMenu {

    private final Computer computer;

    protected ComputerAddCamerasMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
        itemKey = cctv.getCameras().cameraKey;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerAddCamera(page);
    }

    @Override
    protected void onOpen() {
        list(cctv.getCameras().get(p).stream()
                .filter(cam->!computer.getCameras().contains(cctv.getCameras().get(cam)))
                .toList(),camera->{
            Camera cam = cctv.getCameras().get(camera);
            ItemStack item = getItem(cctv.getCustomHeads().get(cam.getSkin()), lang.GUI_COMPUTER_CAMERA_ITEM_NAME + cam.getName());
            Location loc = cam.getLocation();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(List.of("",lang.GUI_COMPUTER_CAMERA_ITEM_X+posFormat.format(loc.getX())
                    +lang.GUI_COMPUTER_CAMERA_ITEM_Y+posFormat.format(loc.getY())
                    +lang.GUI_COMPUTER_CAMERA_ITEM_Z+posFormat.format(loc.getZ())
            ));
            item.setItemMeta(meta);
            inv.addItem(item);
        });
    }

    @Override
    protected void onClick(String name, ClickType click) {
        Camera camera = cctv.getCameras().get(name);
        computer.addCamera(camera);
        open();
        p.sendMessage(lang.COMPUTER_CAMERA_ADDED);
    }
}
