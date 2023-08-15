package io.github.tanguygab.cctv.menus.cameras;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.menus.ListMenu;
import io.github.tanguygab.cctv.utils.CustomHeads;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;


public class CameraSkinMenu extends ListMenu {

    private final Camera camera;
    private final CustomHeads heads;

    public CameraSkinMenu(Player p, Camera camera) {
        super(p);
        this.camera = camera;
        heads = cctv.getCustomHeads();
        itemKey = cctv.getCameras().cameraKey;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiCameraSkin(page);
    }

    @Override
    protected void onOpen() {
        updateCameraItem();
        List<String> list = heads.getHeads();
        list.add(lang.GUI_CAMERA_SKIN_DEFAULT);
        list(list,skin->inv.addItem(getItem(heads.get(skin),skin)));
    }

    @Override
    protected void onClick(String name, ClickType click) {
        camera.setSkin(lang.GUI_CAMERA_SKIN_DEFAULT);
        updateCameraItem();
    }

    private void updateCameraItem() {
        inv.setItem(0, getItem(heads.get(camera.getSkin()),lang.GUI_CAMERA_SKIN_CURRENT+camera.getSkin()));
    }
}
