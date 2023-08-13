package io.github.tanguygab.cctv.managers;

import dev.lone.itemsadder.api.CustomStack;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.ItemsAdderEvents;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.computers.ComputerMainMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComputerManager extends Manager<Computer> {

    public ItemStack COMPUTER_ITEM;
    public ItemStack ADMIN_COMPUTER_ITEM;

    public ComputerManager() {
        super("computers.yml");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        COMPUTER_ITEM = loadComputerMat("block",Material.NETHER_BRICK_STAIRS,lang.COMPUTER_ITEM_NAME);
        ConfigurationFile groupCfg = null;
        File groupFile = new File(cctv.getDataFolder(), "cameragroups.yml");
        try {if (groupFile.exists()) groupCfg = new YamlConfigurationFile(null, groupFile);}
        catch (Exception e) {e.printStackTrace();}
        ConfigurationFile finalGroupCfg = groupCfg;

        if (cctv.getConfiguration().getBoolean("computer.admin-computer.enabled",false))
            ADMIN_COMPUTER_ITEM = loadComputerMat("admin-computer.block",Material.POLISHED_BLACKSTONE_STAIRS,lang.COMPUTER_ITEM_NAME_ADMIN);

        Map<String,Object> map = file.getValues();
        map.forEach((id, cfg)->{
            Map<String,Object> config = (Map<String, Object>) cfg;
            String owner = String.valueOf(config.get("owner"));
            World world = cctv.getServer().getWorld(String.valueOf(config.get("world")));
            double x = (double) config.get("x");
            double y = (double) config.get("y");
            double z = (double) config.get("z");
            boolean publik = (boolean) config.getOrDefault("public",false);
            boolean admin = (boolean) config.getOrDefault("admin",false);

            List<String> cameras = null;
            if (config.containsKey("camera-group")) {
                String group = String.valueOf(config.get("camera-group"));
                config.remove("camera-group");
                if (finalGroupCfg != null && finalGroupCfg.hasConfigOption(group+".cameras"))
                    cameras = finalGroupCfg.getStringList(group+".cameras");
            }
            if (cameras == null) cameras = config.containsKey("cameras") ? (List<String>) config.get("cameras") : new ArrayList<>();
            List<String> allowedPlayers = config.containsKey("allowed-players") ? (List<String>) config.get("allowed-players") : new ArrayList<>();

            create(id,owner,new Location(world,x,y,z),cameras,allowedPlayers,publik,admin);
        });
        if (groupFile.exists()) groupFile.delete();
    }


    private ItemStack loadComputerMat(String path, Material def, String lang) {
        ItemStack item = loadComputerMat0(path,def,lang);
        return item == null ? CCTVMenu.getItem(def,lang) : item;
    }
    private ItemStack loadComputerMat0(String path, Material def, String lang) {
        String mat = cctv.getConfiguration().getString("computer."+path, def.toString());
        if (mat.startsWith("itemsadder:")) {
            if (!cctv.getServer().getPluginManager().isPluginEnabled("ItemsAdder"))
                return null;

            cctv.getServer().getPluginManager().registerEvents(new ItemsAdderEvents(this,mat.substring(11)),cctv);
            CustomStack stack = CustomStack.getInstance(mat.substring(11));
            if (stack != null && stack.isBlock()) {
                cctv.getLogger().info("ItemsAdder item "+stack.getNamespace()+" loaded!");
                return stack.getItemStack();
            }
            cctv.getLogger().info("Using ItemsAdder item for computer, waiting for ItemsAdder to load.");
            return null;
        }
        if (mat.startsWith("head:"))
            return Heads.createSkull(mat.substring(5),lang);

        Material material = Material.getMaterial(mat);
        if (material == null) {
            cctv.getLogger().info("Invalid material for computer! Defaulting to Nether Brick Stairs...");
            return null;
        }
        return CCTVMenu.getItem(material, lang);
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

    private Computer create(String id, String owner, Location loc, List<String> cameras, List<String> allowedPlayers, boolean publik, boolean admin) {
        for (Computer computer : values())
            if (loc.equals(computer.getLocation()) || computer.getId().equals(id))
                return null;

        id = id == null || id.equals("") ? getRandomID() : id;
        Computer computer = new Computer(id,loc,owner,cameras,allowedPlayers,publik,admin);
        put(id,computer);
        return computer;
    }

    public void create(String id, Player p, Location loc, boolean admin) {
        Computer computer = create(id,p.getUniqueId().toString(),loc, new ArrayList<>(), new ArrayList<>(),false,admin && p.hasPermission("cctv.admin.computer"));
        if (computer == null) p.sendMessage(lang.COMPUTER_ALREADY_EXISTS);
        else p.sendMessage(lang.COMPUTER_CREATE+"\n"+lang.getComputerID(computer.getId()));
    }

    public void open(Player p, Computer computer) {
        cctv.openMenu(p,new ComputerMainMenu(p,computer));
    }

}
