package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComputerManager extends Manager<Computer> {

    public static Material COMPUTER_MATERIAL;
    
    public ComputerManager() {
        super("computers.yml");
    }

    @Override
    public void load() {
        Map<String,Object> map = file.getValues();
        map.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            World world = Bukkit.getServer().getWorld(config.get("world")+"");
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");

            String group = config.containsKey("group") ? config.get("group")+"" : null;
            List<String> allowedPlayers = config.containsKey("group") ? (List<String>) config.get("group") : new ArrayList<>();

            create(id,owner,new Location(world,x,y,z),group,allowedPlayers);

        });
    }

    @Override
    public void unload() {
        map.forEach((id, computer)->{
            Location loc = computer.getLocation();
            file.set(id+".owner", computer.getOwner());
            file.set(id + ".world", loc.getWorld());
            file.set(id + ".x", loc.getX());
            file.set(id + ".y", loc.getY());
            file.set(id + ".z", loc.getZ());
            if (computer.getCameraGroup() != null) file.set(id + ".camera-group", computer.getCameraGroup().getId());
            if (!computer.getAllowedPlayers().isEmpty()) file.set(id + ".allowed-players", computer.getAllowedPlayers());
        });
    }

    @Override
    public void delete(String name, Player player) {
        Computer computer = get(name);
        if (computer == null) {
            player.sendMessage(lang.COMPUTER_NOT_FOUND);
            return;
        }
        map.remove(name);
        player.sendMessage(lang.COMPUTER_DELETE);
    }

    public boolean exists(Location loc) {
        return get(loc) != null;
    }
    public Computer get(Location loc) {
        for (Computer computer : values()) {
            if (computer.getLocation().equals(loc)) return computer;
        }
        return null;
    }
    public Computer getLast(Player p) {
        return get(computerfunctions.getLastClickedComputerFromPlayer(p));
    }

    private Computer create(String id, String owner, Location loc, String group, List<String> allowedPlayers) {
        for (Computer computer : values()) {
            if (loc.equals(computer.getLocation()) || computer.getId().equals(id))
                return null;
        }
        id = id == null || id.equals("") ? Utils.getRandomNumber(9999, "computer")+"" : id;
        Computer computer = new Computer(id,loc,owner,group,allowedPlayers);
        map.put(id,computer);
        return computer;
    }

    public void create(String id, Player p, Location loc) {
        Computer computer = create(id,p.getUniqueId().toString(),loc, null, new ArrayList<>());
        if (computer != null) {
            p.sendMessage(lang.COMPUTER_CREATE);
            p.sendMessage(lang.getComputerID(computer.getId()));
        } else p.sendMessage(lang.COMPUTER_ALREADY_EXISTS);
    }
}
