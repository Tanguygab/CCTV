package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.utils.Utils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Computer extends ID {

    @Getter private final Location location;
    @Getter private String owner;
    @Getter private final List<Camera> cameras;
    @Getter private final List<String> allowedPlayers;
    private boolean publik;
    @Getter private final boolean admin;

    public Computer(String name, Location location, String owner, List<String> cameras, List<String> allowedPlayers, boolean publik, boolean admin) {
        super(name,CCTV.getInstance().getComputers());
        this.location = location;
        set("world", location.getWorld().getName());
        set("x", location.getX());
        set("y", location.getY());
        set("z", location.getZ());
        setOwner(owner);
        this.cameras = new ArrayList<>();
        cameras.forEach(str->{
            Camera cam = CCTV.getInstance().getCameras().get(str);
            if (cam != null) this.cameras.add(cam);
        });
        set("cameras", cameras.isEmpty() ? null : Utils.list(cameras));
        setPublic(publik);
        this.allowedPlayers = allowedPlayers;
        set("allowed-players", allowedPlayers.isEmpty() ? null : allowedPlayers);
        set("admin",this.admin = admin);
    }

    @Override
    protected void save() {
        set("world", location.getWorld().getName());
        set("x", location.getX());
        set("y", location.getY());
        set("z", location.getZ());
        setOwner(owner);
        saveCams();
        set("allowed-players", allowedPlayers.isEmpty() ? null : allowedPlayers);
    }

    public void setOwner(String owner) {
        this.owner = owner;
        set("owner",owner);
    }

    public void saveCams() {
        set("cameras", cameras.isEmpty() ? null : Utils.list(cameras));
    }
    public void addCamera(Camera cam) {
        cameras.add(cam);
        saveCams();
    }
    public void removeCamera(Camera cam) {
        cameras.remove(cam);
        saveCams();
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

    public boolean canUse(Player player) {
        return publik || allowedPlayers.contains(player.getUniqueId().toString()) || owner.equals(player.getUniqueId().toString());
    }
}
