package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.menus.computers.ComputerMainMenu;
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
        Material mat = Material.getMaterial(cctv.getConfiguration().getString("computer_block","NETHER_BRICK_STAIRS"));
        ComputerManager.COMPUTER_MATERIAL = mat == null ? Material.NETHER_BRICK_STAIRS : mat;
        
        Map<String,Object> map = file.getValues();
        map.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            World world = Bukkit.getServer().getWorld(config.get("world")+"");
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");

            String group = config.containsKey("camera-group") ? config.get("camera-group")+"" : null;
            List<String> allowedPlayers = config.containsKey("allowed-players") ? (List<String>) config.get("allowed-players") : new ArrayList<>();

            create(id,owner,new Location(world,x,y,z),group,allowedPlayers);

        });
    }

    @Override
    public void delete(String name, Player player) {
        Computer computer = get(name);
        if (computer == null) {
            player.sendMessage(lang.COMPUTER_NOT_FOUND);
            return;
        }
        delete(name);
        player.sendMessage(lang.COMPUTER_DELETE);
    }

    public boolean exists(Location loc) {
        return get(loc) != null;
    }
    public Computer get(Location loc) {
        for (Computer computer : values())
            if (computer.getLocation().equals(loc))
                return computer;
        return null;
    }
    public List<String> get(Player p) {
        List<String> list = new ArrayList<>();
        for (Computer computer : values())
            if (computer.getOwner().equals(p.getUniqueId().toString()))
                list.add(computer.getId());
        return list;
    }
    public Computer getLast(Player p) {
        return Listener.lastClickedComputer.get(p);
    }
    public void setLast(Player p, Computer computer) {
        Listener.lastClickedComputer.put(p,computer);
    }

    private Computer create(String id, String owner, Location loc, String group, List<String> allowedPlayers) {
        for (Computer computer : values())
            if (loc.equals(computer.getLocation()) || computer.getId().equals(id))
                return null;

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

    public void open(Player p, Computer computer) {
        cctv.openMenu(p,new ComputerMainMenu(p,computer));
    }

    public void teleport(Player p, Computer computer) {
        Location loc = computer.getLocation().clone();
        loc.add(1.0D,0.5D,0.5D);
        p.teleport(loc);
    }
}
