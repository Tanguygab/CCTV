package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class Computer extends ID {

    private final Location loc;
    private String owner;
    private CameraGroup cameraGroup;
    private final List<String> allowedPlayers;

    public Computer(String name, Location loc, String owner, String group, List<String> allowedPlayers) {
        super(name);
        this.loc = loc;
        this.owner = owner;
        this.cameraGroup = CCTV.get().getCameraGroups().get(group);
        this.allowedPlayers = allowedPlayers;
    }

    public Location getLocation() {
        return loc;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public CameraGroup getCameraGroup() {
        return cameraGroup;
    }
    public void setCameraGroup(CameraGroup cameraGroup) {
        this.cameraGroup = cameraGroup;
    }

    public List<String> getAllowedPlayers() {
        return allowedPlayers;
    }

    public boolean canUse(OfflinePlayer player) {
        return allowedPlayers.contains(player.getUniqueId().toString()) || owner.equals(player.getUniqueId().toString());
    }
}
