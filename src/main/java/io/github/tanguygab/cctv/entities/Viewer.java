package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.utils.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Viewer extends ID {

    private final ItemStack[] inv;
    private Camera camera;
    private CameraGroup group;
    private boolean nightVision;

    public Viewer(Player p, Camera camera, CameraGroup group) {
        super(p.getUniqueId().toString(),CCTV.get().getViewers());
        inv = p.getInventory().getContents().clone();
        this.camera = camera;
        this.group = group;
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public Camera getCamera() {
        return camera;
    }
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public CameraGroup getGroup() {
        return group;
    }
    public void setGroup(CameraGroup group) {
        this.group = group;
    }

    public boolean hasNightVision() {
        return nightVision;
    }
    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        Player p = CCTV.get().getViewers().get(this);
        if (nightVision) {
            p.hideEntity(CCTV.get(), camera.getArmorStand());
            NMSUtils.setCameraPacket(p,camera.getCreeper());
            return;
        }
        p.showEntity(CCTV.get(), camera.getArmorStand());
        NMSUtils.setCameraPacket(p, camera.getArmorStand());
    }
}
