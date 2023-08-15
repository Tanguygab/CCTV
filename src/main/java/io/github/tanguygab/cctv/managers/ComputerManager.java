package io.github.tanguygab.cctv.managers;

import dev.lone.itemsadder.api.CustomStack;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
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

        if (cctv.getConfiguration().getBoolean("computer.admin-computer.enabled",false))
            ADMIN_COMPUTER_ITEM = loadComputerMat("admin-computer.block",Material.POLISHED_BLACKSTONE_STAIRS,lang.COMPUTER_ITEM_NAME_ADMIN,true);

        file.getValues().keySet().forEach(this::loadFromConfig);
    }

    @Override
    public void unload() {
        values().forEach(this::saveToConfig);
    }

    @Override
    protected void loadFromConfig(String name) {
        String owner = file.getString(name+".owner");
        boolean publik = file.getBoolean(name+".public",false);
        boolean admin = file.getBoolean(name+".admin",false);

        List<String> cameras = null;
        if (file.hasConfigOption(name+".camera-group")) {
            String group = String.valueOf(file.getString(name + ".camera-group"));
            file.set(name + ".camera-group", null);
            cameras = new ArrayList<>();
            cameras.add("group."+group);
        }
        if (cameras == null) cameras = file.getStringList(name+".cameras");
        List<String> allowedPlayers = file.getStringList(name+".allowed-players");

        create(name,owner,Utils.loadLocation(name,file),cameras,allowedPlayers,publik,admin);
    }

    @Override
    protected void saveToConfig(Computer computer) {
        String name = computer.getName();
        set(name,"owner",computer.getOwner());
        Location loc = computer.getLocation();
        set(name,"world", Objects.requireNonNull(loc.getWorld()).getName());
        set(name,"x",loc.getX());
        set(name,"y",loc.getY());
        set(name,"z",loc.getZ());
        set(name,"cameras",computer.getCameras().stream().map(c->(c instanceof CameraGroup ? "group.":"")+c.getName()).toList());
        set(name,"allowed-players",computer.getAllowedPlayers());
        set(name,"public",computer.isPublik());
        set(name,"admin",computer.isAdmin());
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
                list.add(computer.getName());
        return list;
    }

    public Computer create(String id, String owner, Location loc, List<String> cameras, List<String> allowedPlayers, boolean publik, boolean admin) {
        List<Computable> cameras0 = new ArrayList<>();
        cameras.forEach(str->{
            Computable computable = str.startsWith("group.") ? cctv.getGroups().get(str.substring(6)) : cctv.getCameras().get(str);
            if (computable != null) cameras0.add(computable);
        });
        Computer computer = new Computer(id,loc,owner,cameras0,allowedPlayers,publik,admin);
        put(id,computer);
        return computer;
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
        saveToConfig(create(id,p.getUniqueId().toString(),loc,cameras,allowedPlayers,publik,admin));
        p.sendMessage(lang.COMPUTER_CREATE,lang.getComputerName(id));
    }

    public ItemStack breakComputer(Computer computer) {
        ItemStack item = (computer.isAdmin() ? ADMIN_COMPUTER_ITEM : COMPUTER_ITEM).clone();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (!computer.getCameras().isEmpty()) {
            String cameras = computer.getCameras().stream().map(Computable::getName).collect(Collectors.joining("."));
            data.set(computerCamerasKey, PersistentDataType.STRING, cameras);
        }
        if (!computer.getAllowedPlayers().isEmpty()) {
            String allowedPlayers = String.join(",", computer.getAllowedPlayers());
            data.set(computerPlayersKey, PersistentDataType.STRING, allowedPlayers);
        }
        data.set(computerPublicKey,PersistentDataType.BOOLEAN,computer.isPublik());
        data.set(computerKey,PersistentDataType.STRING,computer.isAdmin() ? "admin" : "normal");
        item.setItemMeta(meta);
        return item;
    }

    public void open(Player p, Computer computer) {
        cctv.openMenu(p,new ComputerMainMenu(p,computer));
    }

}
