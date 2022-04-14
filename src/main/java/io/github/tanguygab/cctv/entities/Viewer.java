package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Viewer extends ID {

    private final ItemStack[] inv;
    private Camera camera;
    private CameraGroup group;
    private boolean nightVision;
    private boolean canExit;

    public Viewer(Player p, Camera camera, CameraGroup group) {
        super(p.getUniqueId().toString(),CCTV.get().getViewers());
        inv = p.getInventory().getContents().clone();
        this.camera = camera;
        this.group = group;
        canExit = true;
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
        CCTV cctv = CCTV.get();
        Player p = cctv.getViewers().get(this);
        if (cctv.getCameras().OLD_VIEW) {
            if (nightVision) p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,1000000,0));
            else p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return;
        }
        if (nightVision) {
            p.hideEntity(cctv, camera.getArmorStand());
            cctv.getNMS().setCameraPacket(p,camera.getCreeper());
            return;
        }
        p.showEntity(cctv, camera.getArmorStand());
        cctv.getNMS().setCameraPacket(p, camera.getArmorStand());
    }

    public void setCanExit(boolean canExit) {
        this.canExit = canExit;
    }
    public boolean canExit() {
        return canExit;
    }
}
