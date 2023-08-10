package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Computer extends ID {

    private final Location loc;
    private String owner;
    private final List<Camera> cameras;
    private final List<String> allowedPlayers;
    private boolean publik;
    private final boolean admin;

    public Computer(String name, Location loc, String owner, List<String> cameras, List<String> allowedPlayers, boolean publik, boolean admin) {
        super(name,CCTV.getInstance().getComputers());
        this.loc = loc;
        set("world", loc.getWorld().getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
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
        set("world", loc.getWorld().getName());
        set("x", loc.getX());
        set("y", loc.getY());
        set("z", loc.getZ());
        setOwner(owner);
        saveCams();
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


    public List<Camera> getCameras() {
        return cameras;
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

    public boolean canUse(Player player) {
        return publik || allowedPlayers.contains(player.getUniqueId().toString()) || owner.equals(player.getUniqueId().toString());
    }

    public boolean isAdmin() {
        return admin;
    }
}
