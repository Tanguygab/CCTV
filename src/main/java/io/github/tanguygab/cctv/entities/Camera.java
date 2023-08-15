package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.Objects;

@Getter
public class Camera implements Computable {

    @Setter private String name;
    @Setter private String owner;
    private Location location;
    private boolean enabled;
    private boolean shown;
    private String skin;
    @Setter private ArmorStand armorStand;
    @Setter private Creeper creeper;
    private final BossBar bossbar;

    public Camera(String name, String owner, Location loc, boolean enabled, boolean shown, ArmorStand armorStand, Creeper creeper, String skin) {
        this.name = name;
        this.owner = owner;
        this.armorStand = armorStand;
        this.creeper = creeper;
        setLocation(loc);
        this.enabled = enabled;
        this.shown = shown;
        this.bossbar = CCTV.getInstance().getViewers().BOSSBAR ? Bukkit.getServer().createBossBar(name, BarColor.YELLOW, BarStyle.SOLID) : null;
        this.skin = skin;
    }

    @Override
    public boolean contains(Computable computable) {
        return computable == this;
    }
    @Override
    public Camera get(Viewer viewer) {
        return this;
    }

    public boolean rename(String newName) {
        if (CCTV.getInstance().getCameras().rename(name,newName)) {
            name = newName;
            armorStand.setCustomName("CAM-" + name);
            return true;
        }
        return false;
    }

    public void setLocation(Location loc) {
        this.location = loc;
        if (armorStand != null) {
            armorStand.teleport(loc);
            armorStand.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
        }
        if (creeper != null) {
            loc.add(0, 0.5, 0);
            creeper.teleport(loc);
            loc.add(0, -0.5, 0);
        }
    }

    public boolean rotateHorizontally(int degrees) {
        Location asLoc = armorStand.getLocation();
        float newYaw = Math.round(asLoc.getYaw() + degrees);
        float yaw = location.getYaw();
        float check = yaw > 359.0F
                ? yaw - 360.0F
                : yaw;

        if (newYaw < Math.round(check-36.0F) || newYaw > Math.round(check+36.0F)) return false;
        asLoc.setYaw(newYaw);
        armorStand.teleport(asLoc);
        if (creeper != null) creeper.teleport(asLoc.clone().add(0,0.5,0));
        return true;
    }
    public boolean rotateVertically(int degrees) {
        float pitch = location.getPitch();
        float newPitch = Math.round(pitch + degrees);
        if (newPitch < -45 || newPitch > 45) return false;
        location.setPitch(newPitch);
        setLocation(location);
        return true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) return;
        LanguageFile lang = CCTV.getInstance().getLang();
        for (Viewer viewer : CCTV.getInstance().getViewers().values()) {
            if (viewer.getCamera() != this) continue;
            Player target = viewer.getPlayer();
            if (!target.hasPermission("cctv.camera.view.override") && !target.hasPermission("cctv.admin")) {
                target.sendTitle(lang.CAMERA_OFFLINE,"",0,15,0);
                CCTV.getInstance().getCameras().disconnectFromCamera(target);
                continue;
            }
            target.sendMessage(lang.CAMERA_OFFLINE_OVERRIDE);
        }
    }

    public void setShown(boolean shown) {
        this.shown = shown;
        if (armorStand == null) return;
        Objects.requireNonNull(armorStand.getEquipment()).setHelmet(shown ? CCTV.getInstance().getCustomHeads().get(skin) : null);
    }

    public void setSkin(String skin) {
        this.skin = skin;
        setShown(shown);
        if (bossbar != null) bossbar.setColor(CCTV.getInstance().getCustomHeads().getBarColor(skin));
    }

    public boolean is(Entity entity) {
        if (entity instanceof Creeper c && creeper != null) {
            if (creeper.getUniqueId().equals(c.getUniqueId())) {
                if (creeper != c) creeper = c;
                return true;
            }
        }
        if (entity instanceof ArmorStand as && armorStand != null) {
            if (armorStand.getUniqueId().equals(as.getUniqueId())) {
                if (armorStand != as) armorStand = as;
                return true;
            }
        }
        return false;
    }
}
