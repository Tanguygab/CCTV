package io.github.tanguygab.cctv.entities;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class Camera extends ID {

    private String owner;
    private Location loc;
    private boolean enabled;
    private boolean shown;
    private final ArmorStand armorStand;

    public Camera(String name, String owner, Location loc, boolean enabled, boolean shown, ArmorStand armorStand) {
        super(name);
        this.owner = owner;
        this.loc = loc;
        this.enabled = enabled;
        this.shown = shown;
        this.armorStand = armorStand;
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
    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isShown() {
        return shown;
    }
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
