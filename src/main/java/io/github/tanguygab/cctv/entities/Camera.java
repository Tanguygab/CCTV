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
import java.util.UUID;

public class Camera extends ID {

    @Getter private String owner;
    @Getter private Location location;
    @Getter private boolean enabled;
    @Getter private boolean shown;
    @Getter private String skin;
    @Getter @Setter private ArmorStand armorStand;
    @Getter @Setter private Creeper creeper;
    @Getter private BossBar bossbar;

    public Camera(String name, String owner, Location loc, boolean enabled, boolean shown, ArmorStand armorStand, Creeper creeper, String skin) {
        super(name,CCTV.getInstance().getCameras());
        setOwner(owner);
        this.armorStand = armorStand;
        this.creeper = creeper;
        setLocation(loc);
        setEnabled(enabled);
        setShown(shown);
        if (CCTV.getInstance().getViewers().BOSSBAR)
            this.bossbar = Bukkit.getServer().createBossBar(name, BarColor.YELLOW, BarStyle.SOLID);
        setSkin(skin);
    }

    @Override
    protected void save() {
        setOwner(owner);
        setLocation(location);
        setEnabled(enabled);
        setShown(shown);
        setSkin(skin);
    }

    public boolean rename(String newName) {
        if (CCTV.getInstance().getCameras().exists(newName)) {
            return false;
        }
        setId(newName);
        CCTV.getInstance().getComputers().values().forEach(computer->{
            if (computer.getCameras().contains(this))
                computer.saveCams();
        });
        armorStand.setCustomName("CAM-"+getId());
        return true;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        set("owner",owner);
    }

    public void setLocation(Location loc) {
        this.location = loc;
        set("world", Objects.requireNonNull(loc.getWorld()).getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
        set("pitch", loc.getPitch());
        set("yaw", loc.getYaw());
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
        asLoc.add(0,0.5,0);
        if (creeper != null)
            creeper.teleport(asLoc);
        asLoc.add(0,-0.5,0);
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
        set("enabled", enabled);
        if (enabled) return;
        LanguageFile lang = CCTV.getInstance().getLang();
        for (Viewer viewer : CCTV.getInstance().getViewers().values()) {
            if (viewer.getCamera() != this) continue;
            Player target = Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
            if (target == null) continue;
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
        set("shown",shown);
        if (armorStand != null)
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(shown ? CCTV.getInstance().getCustomHeads().get(skin) : null);
    }

    public void setSkin(String skin) {
        this.skin = skin;
        set("skin",skin);
        setShown(shown);
        if (bossbar != null) bossbar.setColor(cctv.getCustomHeads().getBarColor(skin));
    }

    public boolean is(Entity entity) {
        if (entity instanceof Creeper c && creeper != null) {
            if (creeper.getUniqueId().equals(c.getUniqueId())) {
                if (creeper != c) creeper = c;
                return true;
            }
        }
        if (entity instanceof ArmorStand as) {
            if (armorStand.getUniqueId().equals(as.getUniqueId())) {
                if (armorStand != as) armorStand = as;
                return true;
            }
        }
        return false;
    }
}
