package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

public class Camera extends ID {

    private String owner;
    private Location loc;
    private boolean enabled;
    private boolean shown;
    private String skin;
    private ArmorStand armorStand;

    public Camera(String name, String owner, Location loc, boolean enabled, boolean shown, ArmorStand armorStand, String skin) {
        super(name,CCTV.get().getCameras());
        setOwner(owner);
        this.armorStand = armorStand;
        setLocation(loc);
        setEnabled(enabled);
        setShown(shown);
        setSkin(skin);
    }

    @Override
    protected void save() {
        setOwner(owner);
        setLocation(loc);
        setEnabled(enabled);
        setShown(shown);
        setSkin(skin);
    }

    public boolean rename(String newName) {
        if (CCTV.get().getCameras().exists(newName)) {
            return false;
        }
        setId(newName);
        CCTV.get().getCameraGroups().values().forEach(g->{
            if (g.getCameras().contains(this))
                g.saveCams();
        });
        armorStand.setCustomName("CAM-"+getId());
        return true;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
        set("owner",owner);
    }

    public Location getLocation() {
        return loc;
    }
    public void setLocation(Location loc) {
        this.loc = loc;
        set("world", loc.getWorld().getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
        set("pitch", loc.getPitch());
        set("yaw", loc.getYaw());
        armorStand.teleport(loc);
        armorStand.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
        tpViewers();
    }
    private void tpViewers() {
        for (Viewer viewer : CCTV.get().getViewers().values()) {
            if (viewer.getCamera() != this) continue;
            Player target = Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
            if (target != null) CCTV.get().getCameras().teleport(this, target);
        }
    }

    public boolean rotateHorizontally(int degrees) {
        Location asLoc = armorStand.getLocation();
        float newYaw = Math.round(asLoc.getYaw() + degrees);
        float yaw = loc.getYaw();
        float check = yaw > 359.0F
                ? yaw - 360.0F
                : yaw;

        if (newYaw < Math.round(check-36.0F) || newYaw > Math.round(check+36.0F)) return false;
        asLoc.setYaw(newYaw);
        armorStand.teleport(asLoc);
        tpViewers();
        return true;
    }
    public boolean rotateVertically(int degrees) {
        float pitch = loc.getPitch();
        float newPitch = Math.round(pitch + degrees);
        if (newPitch < -45 || newPitch > 45) return false;
        loc.setPitch(newPitch);
        setLocation(loc);
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        set("enabled", enabled);
        if (enabled) return;
        LanguageFile lang = CCTV.get().getLang();
        for (Viewer viewer : CCTV.get().getViewers().values()) {
            if (viewer.getCamera() != this) continue;
            Player target = Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
            if (target == null) continue;
            if (!target.hasPermission("cctv.camera.view.override") && !target.hasPermission("cctv.admin")) {
                target.sendTitle(lang.CAMERA_OFFLINE,"",0,15,0);
                CCTV.get().getCameras().unviewCamera(target);
                continue;
            }
            target.sendMessage(lang.CAMERA_OFFLINE_OVERRIDE);
        }
    }

    public boolean isShown() {
        return shown;
    }
    public void setShown(boolean shown) {
        this.shown = shown;
        set("shown",shown);
        armorStand.getEquipment().setHelmet(shown ? CCTV.get().getCustomHeads().get(skin) : null);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    public String getSkin() {
        return skin;
    }
    public void setSkin(String skin) {
        this.skin = skin;
        set("skin",skin);
        setShown(true);
    }
}
