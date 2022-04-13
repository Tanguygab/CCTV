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
    private boolean publik;

    public Computer(String name, Location loc, String owner, String group, List<String> allowedPlayers, boolean publik) {
        super(name,CCTV.get().getComputers());
        this.loc = loc;
        set("world", loc.getWorld().getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
        setOwner(owner);
        setCameraGroup(CCTV.get().getCameraGroups().get(group));
        this.allowedPlayers = allowedPlayers;
        setPublic(publik);
        set("allowed-players", allowedPlayers.isEmpty() ? null : allowedPlayers);
    }

    @Override
    protected void save() {
        set("world", loc.getWorld().getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
        setOwner(owner);
        setCameraGroup(cameraGroup);
        set("allowed-players", allowedPlayers.isEmpty() ? null : allowedPlayers);

    }

    public Location getLocation() {
        return loc;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
        set("owner",owner);
    }

    public CameraGroup getCameraGroup() {
        return cameraGroup;
    }
    public void setCameraGroup(CameraGroup cameraGroup) {
        this.cameraGroup = cameraGroup;
        set("camera-group",cameraGroup == null ? null : cameraGroup.getId());
    }

    public List<String> getAllowedPlayers() {
        return allowedPlayers;
    }
    public void addPlayer(String player) {
        allowedPlayers.add(player);
        set("allowed-players", allowedPlayers);
    }
    public void removePlayer(String player) {
        allowedPlayers.remove(player);
        set("allowed-players", allowedPlayers.isEmpty() ? null : allowedPlayers);
    }

    public boolean isPublic() {
        return publik;
    }
    public void setPublic(boolean publik) {
        this.publik = publik;
        set("public",publik);
    }

    public boolean canUse(OfflinePlayer player) {
        return publik || allowedPlayers.contains(player.getUniqueId().toString()) || owner.equals(player.getUniqueId().toString());
    }
}
