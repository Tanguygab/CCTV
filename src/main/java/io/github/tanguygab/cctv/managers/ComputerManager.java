package io.github.tanguygab.cctv.managers;

import dev.lone.itemsadder.api.CustomStack;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.listeners.ItemsAdderEvents;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.computers.ComputerMainMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ComputerManager extends Manager<Computer> {

    public ItemStack COMPUTER_ITEM;
    public ItemStack ADMIN_COMPUTER_ITEM;
    public final NamespacedKey computerKey = new NamespacedKey(cctv,"computer");
    public final NamespacedKey computerCamerasKey = new NamespacedKey(cctv,"computer-cameras");
    public final NamespacedKey computerPlayersKey = new NamespacedKey(cctv,"computer-players");
    public final NamespacedKey computerPublicKey = new NamespacedKey(cctv,"computer-public");

    public ComputerManager() {
        super("computers.yml");
    }

    @Override
    public void load() {
        COMPUTER_ITEM = loadComputerMat("block",Material.NETHER_BRICK_STAIRS,lang.COMPUTER_ITEM_NAME,false);
        ConfigurationFile groupCfg = null;
        File groupFile = new File(cctv.getDataFolder(), "cameragroups.yml");
        try {if (groupFile.exists()) groupCfg = new YamlConfigurationFile(null, groupFile);}
        catch (Exception e) {e.printStackTrace();}
        ConfigurationFile finalGroupCfg = groupCfg;

        if (cctv.getConfiguration().getBoolean("computer.admin-computer.enabled",false))
            ADMIN_COMPUTER_ITEM = loadComputerMat("admin-computer.block",Material.POLISHED_BLACKSTONE_STAIRS,lang.COMPUTER_ITEM_NAME_ADMIN,true);

        file.getValues().keySet().forEach(id->{
            String owner = file.getString(id+".owner");
            boolean publik = file.getBoolean(id+".public",false);
            boolean admin = file.getBoolean(id+".admin",false);

            List<String> cameras = null;
            if (file.hasConfigOption(id+".camera-group")) {
                String group = String.valueOf(file.getString(id + ".camera-group"));
                file.set(id + ".camera-group", null);
                if (finalGroupCfg != null && finalGroupCfg.hasConfigOption(group + ".cameras"))
                    cameras = finalGroupCfg.getStringList(group + ".cameras");
            }
            if (cameras == null) cameras = file.getStringList(id+".cameras", new ArrayList<>());
            List<String> allowedPlayers = file.getStringList(id+".allowed-players", new ArrayList<>());

            create(id,owner,Utils.loadLocation(id,file),cameras,allowedPlayers,publik,admin);
        });
        if (groupFile.exists()) groupFile.delete();
    }


    private ItemStack loadComputerMat(String path, Material def, String lang, boolean admin) {
        ItemStack item = loadComputerMat0(path,def,lang,admin);
        item = item == null ? CCTVMenu.getItem(def,lang) : item;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(computerKey,PersistentDataType.STRING,admin ? "admin" : "normal");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack loadComputerMat0(String path, Material def, String lang, boolean admin) {
        String mat = cctv.getConfiguration().getString("computer."+path, def.toString());
        if (mat.startsWith("itemsadder:")) {
            if (!cctv.getServer().getPluginManager().isPluginEnabled("ItemsAdder"))
                return null;

            cctv.getServer().getPluginManager().registerEvents(new ItemsAdderEvents(this,mat.substring(11),admin),cctv);
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
            cctv.getLogger().warning("Invalid material for computer! Defaulting to Nether Brick Stairs...");
            return null;
        }
        return CCTVMenu.getItem(material, lang);
    }
    public String isComputer(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(computerKey, PersistentDataType.STRING)) return null;
        return meta.getPersistentDataContainer().get(computerKey, PersistentDataType.STRING);
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

    public void create(String id, String owner, Location loc, List<String> cameras, List<String> allowedPlayers, boolean publik, boolean admin) {
        Computer computer = new Computer(id,loc,owner,cameras,allowedPlayers,publik,admin);
        put(id,computer);
    }

    public void create(ItemStack item, Player p, Location loc) {
        assert item.getItemMeta() != null;
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        List<String> cameras = new ArrayList<>();
        if (data.has(computerCamerasKey,PersistentDataType.STRING))
            cameras.addAll(Arrays.asList(Objects.requireNonNull(data.get(computerCamerasKey, PersistentDataType.STRING)).split("\\.")));
        List<String> allowedPlayers = new ArrayList<>();
        if (data.has(computerPlayersKey,PersistentDataType.STRING))
            allowedPlayers.addAll(Arrays.asList(Objects.requireNonNull(data.get(computerPlayersKey, PersistentDataType.STRING)).split(",")));
        boolean admin = "admin".equals(data.get(computerKey,PersistentDataType.STRING));
        boolean publik = Boolean.TRUE.equals(data.get(computerPublicKey, PersistentDataType.BOOLEAN));

        if (!p.hasPermission("cctv.admin.computer")) admin = false;
        String id = getRandomID();
        create(id,p.getUniqueId().toString(),loc,cameras,allowedPlayers,publik,admin);
        p.sendMessage(lang.COMPUTER_CREATE+"\n"+lang.getComputerID(id));
    }

    public ItemStack breakComputer(Computer computer) {
        ItemStack item = (computer.isAdmin() ? ADMIN_COMPUTER_ITEM : COMPUTER_ITEM).clone();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (!computer.getCameras().isEmpty()) {
            String cameras = computer.getCameras().stream().map(Camera::getId).collect(Collectors.joining("."));
            data.set(computerCamerasKey, PersistentDataType.STRING, cameras);
        }
        if (!computer.getAllowedPlayers().isEmpty()) {
            String allowedPlayers = String.join(",", computer.getAllowedPlayers());
            data.set(computerPlayersKey, PersistentDataType.STRING, allowedPlayers);
        }
        data.set(computerPublicKey,PersistentDataType.BOOLEAN,computer.isPublic());
        data.set(computerKey,PersistentDataType.STRING,computer.isAdmin() ? "admin" : "normal");
        item.setItemMeta(meta);
        return item;
    }

    public void open(Player p, Computer computer) {
        cctv.openMenu(p,new ComputerMainMenu(p,computer));
    }

}
