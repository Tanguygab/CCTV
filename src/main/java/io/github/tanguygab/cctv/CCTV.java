package io.github.tanguygab.cctv;

import io.github.tanguygab.cctv.commands.CameraCmd;
import io.github.tanguygab.cctv.commands.ComputerCmd;
import io.github.tanguygab.cctv.commands.GroupCmd;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import io.github.tanguygab.cctv.listeners.ViewersEvents;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CCTV extends JavaPlugin {

    private static CCTV instance;
    public static CCTV get() {
        return instance;
    }

    public boolean debug;

    private ConfigurationFile config;
    private LanguageFile lang;
    public ConfigurationFile getConfiguration() {
        return config;
    }
    public LanguageFile getLang() {
        return lang;
    }

    private CameraManager cameraManager;
    private CameraGroupManager cameraGroupManager;
    private ComputerManager computerManager;
    private ViewerManager viewerManager;

    public CameraManager getCameras() {
        return cameraManager;
    }
    public CameraGroupManager getCameraGroups() {
        return cameraGroupManager;
    }
    public ComputerManager getComputers() {
        return computerManager;
    }
    public ViewerManager getViewers() {
        return viewerManager;
    }

    public boolean CISIWP;
    public double CAMERA_HEAD_RADIUS;
    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;

    public final List<Player> chatInput = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        try {
            config = new YamlConfigurationFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));
            lang = new LanguageFile(getResource("language.yml"), new File(getDataFolder(), "language.yml"));
            cameraManager = new CameraManager();
            cameraGroupManager = new CameraGroupManager();
            computerManager = new ComputerManager();
            viewerManager = new ViewerManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadConfig();
        cameraManager.load();
        cameraGroupManager.load();
        computerManager.load();

        loadRecipes();
        Bukkit.getPluginManager().registerEvents(new Listener(),this);
        Bukkit.getPluginManager().registerEvents(new ViewersEvents(),this);

        getLogger().info(".-==--+]- CCTV -[+--==-.");
        getLogger().info("This is the CCTV plugin!");
        getLogger().info("Authors: Timdecoole123, Streampy");
        getLogger().info("For updates check our spigot page");
        getLogger().info("Spigot: https://www.spigotmc.org/resources/cctv.60310/");
        getLogger().info("To report bugs go to the spigot page and send us a message!");
        getLogger().info("NOTE: This plugin is discontinued. There will only be small improvements.");
        getLogger().info("NOTE 2: Hey, I'm Tanguygab, and I'm stupid =) This plugin's code is so weird btw ;-;");
        getLogger().info(".-==--+]-      -[+--==-.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        cameraManager.unload();
        cameraGroupManager.unload();
        computerManager.unload();

        for (Player p : Bukkit.getOnlinePlayers()) {
            cameraManager.unviewCamera(p);
            p.closeInventory();
        }
        getLogger().info("CCTV Plugin has been succesfully Disabled!");
    }

    private void loadRecipes() {
        NamespacedKey key = Utils.cameraKey;
        if (Bukkit.getRecipe(key) == null) {
            ShapedRecipe recipe = new ShapedRecipe(key, Utils.getCamera());
            recipe.shape("RPP", "PDG", "LCP");
            recipe.setIngredient('R', Material.REDSTONE_BLOCK);
            recipe.setIngredient('P', Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
            recipe.setIngredient('D', Material.DISPENSER);
            recipe.setIngredient('G', Material.GLASS_PANE);
            recipe.setIngredient('L', Material.DAYLIGHT_DETECTOR);
            recipe.setIngredient('C', Material.COMPARATOR);
            Bukkit.addRecipe(recipe);
        }
        NamespacedKey key2 = Utils.computerKey;
        if (Bukkit.getRecipe(key2) == null) {
            ShapedRecipe recipe2 = new ShapedRecipe(key2, Utils.getComputer());
            recipe2.shape("ITI", "PGP", "RCR");
            recipe2.setIngredient('I', Material.REDSTONE);
            recipe2.setIngredient('P', Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
            recipe2.setIngredient('G', Material.GLASS_PANE);
            recipe2.setIngredient('C', Material.COMPARATOR);
            recipe2.setIngredient('R', Material.REPEATER);
            recipe2.setIngredient('T', Material.REDSTONE_TORCH);
            Bukkit.addRecipe(recipe2);
        }
    }

    private void loadConfig() {
        Material mat = Material.getMaterial(config.getString("computer_block","NETHER_BRICK_STAIRS"));
        ComputerManager.COMPUTER_MATERIAL = mat == null ? Material.NETHER_BRICK_STAIRS : mat;

        CISIWP = config.getBoolean("camera_inventory_show_item_without_permissions");
        CAMERA_HEAD_RADIUS = config.getDouble("camera_head_radius",0.35D);
        TIME_TO_CONNECT = config.getInt("time_to_connect",3);
        TIME_TO_DISCONNECT = config.getInt("time_to_disconnect",3);
        TIME_FOR_SPOT = config.getInt("time_for_spot",5);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arg = args.length > 1 ? args[0] : "";
        switch (arg) {
            case "camera" -> CameraCmd.onCommand(sender,args);
            case "computer" -> ComputerCmd.onCommand(sender,args);
            case "group" -> GroupCmd.onCommand(sender,args);
            case "debug" -> debug = !debug;
            default -> sender.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD + "Subcommands for /cctv" + ChatColor.YELLOW
                    + "\nplayer"
                    + "\ncamera"
                    + "\ngroup"
                    + "\ncomputer");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String arg = args.length > 1 ? args[0] : "";
        return switch (arg) {
            case "camera" -> CameraCmd.onTabComplete(sender,args);
            case "computer" -> ComputerCmd.onTabComplete(sender,args);
            case "group" -> GroupCmd.onTabComplete(args);
            default -> List.of("camera","computer","group");
        };
    }
}
