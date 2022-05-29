package io.github.tanguygab.cctv.managers;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.computers.ComputerMainMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        COMPUTER_ITEM = loadComputerMat(mat);
        if (COMPUTER_ITEM == null) {
            cctv.getLogger().info("Defaulting to Nether Brick Stairs...");
            COMPUTER_ITEM = CCTVMenu.getItem(Material.NETHER_BRICK_STAIRS, lang.COMPUTER_ITEM_NAME);
        }
        Map<String,Object> map = file.getValues();
        map.forEach((id,cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            World world = cctv.getServer().getWorld(config.get("world")+"");
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");
            boolean publik = (boolean) config.getOrDefault("public",false);

            String group = config.containsKey("camera-group") ? config.get("camera-group")+"" : null;
            List<String> allowedPlayers = config.containsKey("allowed-players") ? (List<String>) config.get("allowed-players") : new ArrayList<>();

            create(id,owner,new Location(world,x,y,z),group,allowedPlayers,publik);

        });
    }

    private ItemStack loadComputerMat(String mat) {
        if (mat.startsWith("itemsadder:")) {
            if (!cctv.getServer().getPluginManager().isPluginEnabled("ItemsAdder"))
                return null;
            CustomStack stack = CustomStack.getInstance(mat.substring(11));
            if (stack == null) {
                cctv.getLogger().info("Invalid ItemsAdder block as computer!");
                return null;
            }
            if (!stack.isBlock()) {
                cctv.getLogger().info("ItemsAdder item for computer is not a block!");
                return null;
            }
            return stack.getItemStack();
        }
        if (mat.startsWith("head:"))
            return Heads.createSkull(mat.substring(5),lang.COMPUTER_ITEM_NAME);

        Material material = Material.getMaterial(mat);
        if (material == null) {
            cctv.getLogger().info("Invalid material for computer!");
            return null;
        }
        return CCTVMenu.getItem(material, lang.COMPUTER_ITEM_NAME);
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

}
