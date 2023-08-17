package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.menus.ListMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComputerAddCamerasMenu extends ListMenu {

    private final Computer computer;
    private final CameraManager cm = cctv.getCameras();

    protected ComputerAddCamerasMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerAddCamera(page);
    }

    @Override
    protected void onOpen() {
        list(cm.get(player).stream()
                .map(cm::get)
                .filter(camera->!computer.getCameras().contains(camera))
                .toList(),camera->{
            ItemStack item = getItem(cctv.getCustomHeads().get(camera.getSkin()),
                    cctv.getCustomHeads().getChatColor(camera.getSkin())+camera.getName());
            Location loc = camera.getLocation();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(List.of("",lang.GUI_COMPUTER_CAMERA_ITEM_X+posFormat.format(loc.getX())
                    +lang.GUI_COMPUTER_CAMERA_ITEM_Y+posFormat.format(loc.getY())
                    +lang.GUI_COMPUTER_CAMERA_ITEM_Z+posFormat.format(loc.getZ())
            ));
            setMeta(meta,camera.getName());
            item.setItemMeta(meta);
            inv.addItem(item);
        });
    }

    @Override
    protected void onClick(String name, ClickType click) {
        Camera camera = cm.get(name);
        if (camera != null) computer.addCamera(camera);
        open();
        player.sendMessage(lang.getEditCameras(true,true,true,true));
    }
}
