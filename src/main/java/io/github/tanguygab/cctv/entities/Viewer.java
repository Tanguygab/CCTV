package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@Getter
public class Viewer {

    private final UUID uuid;
    private final Player player;
    private final ItemStack[] inventory;
    private Computable group;
    private Camera camera;
    @Setter private Computer computer;
    private boolean nightVision;

    public Viewer(Player player, Computable camera, Computer computer) {
        this.uuid = player.getUniqueId();
        this.player = player;
        inventory = player.getInventory().getContents().clone();
        setCamera(camera,false);
        this.computer = computer;
    }

    public void setCamera(Computable cam, boolean previous) {
        if (CCTV.getInstance().getViewers().BOSSBAR && cam != group) {
            if (group != null) group.getBossbar().removePlayer(player);
            if (cam != null) cam.getBossbar().addPlayer(player);
        }
        if (cam == null) {
            CCTV.getInstance().getNms().setCameraPacket(player,player);
            player.getInventory().setContents(inventory);
            return;
        }

        camera = cam.get(camera,previous);
        CCTV.getInstance().getNms().setCameraPacket(player, camera.getArmorStand());
        boolean nv = nightVision;
        if (nv) setNightVision(false);
        group = cam;
        if (nv) setNightVision(true);
    }

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
