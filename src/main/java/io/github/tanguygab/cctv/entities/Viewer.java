package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Viewer {

    @Getter private final UUID uuid;
    @Getter private final Player player;
    @Getter private final ItemStack[] inventory;
    @Getter private Computable group;
    @Getter private Camera camera;
    @Getter @Setter private Computer computer;
    @Getter private boolean nightVision;

    public Viewer(Player player, Camera camera, Computable group, Computer computer) {
        this.uuid = player.getUniqueId();
        this.player = player;
        inventory = player.getInventory().getContents().clone();
        setCamera(camera);
        this.computer = computer;
    }

    public void setCamera(Camera camera) {

        if (CCTV.getInstance().getViewers().BOSSBAR) {
            if (this.camera != null) this.camera.getBossbar().removePlayer(player);
            if (camera != null) camera.getBossbar().addPlayer(player);
        }
        if (camera == null) {
            CCTV.getInstance().getNms().setCameraPacket(player,player);
            player.getInventory().setContents(inventory);
            return;
        }

        CCTV.getInstance().getNms().setCameraPacket(player, camera.getArmorStand());
        boolean nv = nightVision;
        if (nv) setNightVision(false);
        this.camera = camera;
        if (nv) setNightVision(true);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        if (!CCTV.getInstance().getCameras().EXPERIMENTAL_VIEW) {
            if (nightVision) player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,1000000,0));
            else player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return;
        }
        if (nightVision) {
            player.hideEntity(CCTV.getInstance(), camera.getArmorStand());
            CCTV.getInstance().getNms().setCameraPacket(player,camera.getCreeper());
            return;
        }
        player.showEntity(CCTV.getInstance(), camera.getArmorStand());
        CCTV.getInstance().getNms().setCameraPacket(player, camera.getArmorStand());
    }

}
