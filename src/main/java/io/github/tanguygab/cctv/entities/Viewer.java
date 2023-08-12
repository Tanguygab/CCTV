package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Viewer extends ID {

    @Getter private final Player player;
    @Getter private final ItemStack[] inv;
    @Getter private Camera camera;
    @Getter @Setter private Computer computer;
    private boolean nightVision;
    @Setter private boolean canExit;

    public Viewer(Player p, Camera camera, Computer computer) {
        super(p.getUniqueId().toString(),CCTV.getInstance().getViewers());
        this.player = p;
        inv = p.getInventory().getContents().clone();
        setCamera(camera);
        this.computer = computer;
        canExit = true;
    }

    public void setCamera(Camera camera) {
        if (cctv.getViewers().BOSSBAR) {
            if (this.camera != null) this.camera.getBossbar().removePlayer(player);
            if (camera != null) camera.getBossbar().addPlayer(player);
        }
        if (camera == null) {
            cctv.getNms().setCameraPacket(player,player);
            player.getInventory().setContents(inv);
            return;
        }

        cctv.getNms().setCameraPacket(player, camera.getArmorStand());
        boolean nv = nightVision;
        if (nv) setNightVision(false);
        this.camera = camera;
        if (nv) setNightVision(true);
    }

    public boolean hasNightVision() {
        return nightVision;
    }
    @SuppressWarnings("UnstableApiUsage")
    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        if (!cctv.getCameras().EXPERIMENTAL_VIEW) {
            if (nightVision) player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,1000000,0));
            else player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return;
        }
        if (nightVision) {
            player.hideEntity(cctv, camera.getArmorStand());
            cctv.getNms().setCameraPacket(player,camera.getCreeper());
            return;
        }
        player.showEntity(cctv, camera.getArmorStand());
        cctv.getNms().setCameraPacket(player, camera.getArmorStand());
    }

    public boolean canExit() {
        return canExit;
    }
}
