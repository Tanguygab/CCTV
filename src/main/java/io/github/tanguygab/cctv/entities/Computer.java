package io.github.tanguygab.cctv.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class Computer {

    @Getter private final String name;
    @Getter private final Location location;
    @Getter @Setter private String owner;
    @Getter private final List<Computable> cameras;
    @Getter private final List<String> allowedPlayers;
    @Getter @Setter private boolean publik;
    @Getter private final boolean admin;

    public void addCamera(Computable camera) {
        cameras.add(camera);
    }
    public void removeCamera(Computable camera) {
        cameras.remove(camera);
    }

    public void addPlayer(String player) {
        allowedPlayers.add(player);
    }
    public void removePlayer(String player) {
        allowedPlayers.remove(player);
    }

    public boolean canUse(Player player) {
        return publik || allowedPlayers.contains(player.getUniqueId().toString()) || owner.equals(player.getUniqueId().toString());
    }
}
