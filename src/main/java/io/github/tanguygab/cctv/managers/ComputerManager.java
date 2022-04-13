package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.computers.ComputerMainMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComputerManager extends Manager<Computer> {

    public static ItemStack COMPUTER_ITEM;
    
    public ComputerManager() {
        super("computers.yml");
    }

    @Override
    public void load() {
        String mat = cctv.getConfiguration().getString("computer.block","NETHER_BRICK_STAIRS");
        if (!mat.startsWith("head:")) {
            Material material = Material.getMaterial(mat);
            COMPUTER_ITEM = CCTVMenu.getItem(material == null ? Material.NETHER_BRICK_STAIRS : material, CCTV.get().getLang().COMPUTER_ITEM_NAME);
        } else COMPUTER_ITEM = Heads.createSkull(mat.substring(mat.indexOf(":")+1),CCTV.get().getLang().COMPUTER_ITEM_NAME);
        
        Map<String,Object> map = file.getValues();
        map.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            World world = Bukkit.getServer().getWorld(config.get("world")+"");
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");
            boolean publik = (boolean) config.getOrDefault("public",false);

            String group = config.containsKey("camera-group") ? config.get("camera-group")+"" : null;
            List<String> allowedPlayers = config.containsKey("allowed-players") ? (List<String>) config.get("allowed-players") : new ArrayList<>();

            create(id,owner,new Location(world,x,y,z),group,allowedPlayers,publik);

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

    public boolean exists(Block block) {
        return get(block) != null;
    }
    public Computer get(Block block) {
        if (block.getType() == ComputerManager.COMPUTER_ITEM.getType()
                || (block.getType() == Material.PLAYER_WALL_HEAD
                && ComputerManager.COMPUTER_ITEM.getType() == Material.PLAYER_HEAD))
            for (Computer computer : values())
                if (computer.getLocation().equals(block.getLocation()))
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

    private Computer create(String id, String owner, Location loc, String group, List<String> allowedPlayers,boolean publik) {
        for (Computer computer : values())
            if (loc.equals(computer.getLocation()) || computer.getId().equals(id))
                return null;

        id = id == null || id.equals("") ? Utils.getRandomNumber(9999, "computer")+"" : id;
        Computer computer = new Computer(id,loc,owner,group,allowedPlayers,publik);
        map.put(id,computer);
        return computer;
    }

    public void create(String id, Player p, Location loc) {
        Computer computer = create(id,p.getUniqueId().toString(),loc, null, new ArrayList<>(),false);
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
