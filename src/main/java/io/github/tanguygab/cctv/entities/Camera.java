package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.utils.CustomHeads;
import io.github.tanguygab.cctv.utils.Heads;
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
    private final ArmorStand armorStand;

    public Camera(String name, String owner, Location loc, boolean enabled, boolean shown, ArmorStand armorStand, String skin) {
        super(name);
        this.owner = owner;
        this.loc = loc;
        this.enabled = enabled;
        this.shown = shown;
        this.armorStand = armorStand;
        this.skin = skin;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return loc;
    }
    public void setLocation(Location loc) {
        this.loc = loc;
        armorStand.teleport(loc);
        armorStand.setHeadPose(new EulerAngle(Math.toRadians(loc.getPitch()), 0.0D, 0.0D));
        for (Viewer viewer : CCTV.get().getViewers().values()) {
            if (viewer.getCamera() != this) continue;
            Player target = Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
            if (target != null) CCTV.get().getCameras().teleport(this, target);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        armorStand.getEquipment().setHelmet(shown ? CCTV.get().getCustomHeads().get(skin) : null);
        this.shown = shown;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public String getSkin() {
        return skin;
    }
    public void setSkin(String skin) {
        this.skin = skin;
        setShown(true);
    }
}
