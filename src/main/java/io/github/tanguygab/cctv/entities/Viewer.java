package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Viewer extends ID {

    private final ItemStack[] inv;
    private Camera camera;
    private Computer computer;
    private boolean nightVision;
    private boolean canExit;

    public Viewer(Player p, Camera camera, Computer computer) {
        super(p.getUniqueId().toString(),CCTV.getInstance().getViewers());
        inv = p.getInventory().getContents().clone();
        this.camera = camera;
        this.computer = computer;
        canExit = true;
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public Camera getCamera() {
        return camera;
    }
    public void setCamera(Camera camera) {
        boolean nv = nightVision;
        if (nv) setNightVision(false);
        this.camera = camera;
        if (nv) setNightVision(true);
    }

    public Computer getComputer() {
        return computer;
    }
    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public boolean hasNightVision() {
        return nightVision;
    }
    @SuppressWarnings("UnstableApiUsage")
    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        CCTV cctv = CCTV.getInstance();
        Player p = cctv.getViewers().get(this);
        if (!cctv.getCameras().EXPERIMENTAL_VIEW) {
            if (nightVision) p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,1000000,0));
            else p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return;
        }
        if (nightVision) {
            p.hideEntity(cctv, camera.getArmorStand());
            cctv.getNms().setCameraPacket(p,camera.getCreeper());
            return;
        }
        p.showEntity(cctv, camera.getArmorStand());
        cctv.getNms().setCameraPacket(p, camera.getArmorStand());
    }

    public void setCanExit(boolean canExit) {
        this.canExit = canExit;
    }
    public boolean canExit() {
        return canExit;
    }
}
