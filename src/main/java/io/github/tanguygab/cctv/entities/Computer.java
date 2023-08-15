package io.github.tanguygab.cctv.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@AllArgsConstructor
public class Computer {

    private final String name;
    private final Location location;
    @Setter private String owner;
    private final List<Computable> cameras;
    private final List<String> allowedPlayers;
    @Setter private boolean publik;
    private final boolean admin;

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
