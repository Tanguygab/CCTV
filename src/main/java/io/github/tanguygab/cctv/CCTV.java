package io.github.tanguygab.cctv;

import io.github.tanguygab.cctv.commands.CameraCmd;
import io.github.tanguygab.cctv.commands.ComputerCmd;
import io.github.tanguygab.cctv.commands.GroupCmd;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import io.github.tanguygab.cctv.listeners.ComputersEvents;
import io.github.tanguygab.cctv.listeners.ViewersEvents;
import io.github.tanguygab.cctv.listeners.Listener;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.CustomHeads;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class CCTV extends JavaPlugin {

    private static CCTV instance;
    public static CCTV get() {
        return instance;
    }

    public boolean debug;

    private ConfigurationFile config;
    private LanguageFile lang;
    private CustomHeads customHeads;
    public ConfigurationFile getConfiguration() {
        return config;
    }
    public LanguageFile getLang() {
        return lang;
    }
    public CustomHeads getCustomHeads() {
        return customHeads;
    }

    private CameraCmd cameraCmd;
    private GroupCmd groupCmd;
    private ComputerCmd computerCmd;

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

        customHeads = new CustomHeads();

        cameraCmd = new CameraCmd();
        groupCmd = new GroupCmd();
        computerCmd = new ComputerCmd();

        cameraManager.load();
        cameraGroupManager.load();
        computerManager.load();
        viewerManager.load();

        loadRecipes();
        Bukkit.getPluginManager().registerEvents(new Listener(),this);
        Bukkit.getPluginManager().registerEvents(new ViewersEvents(),this);
        Bukkit.getPluginManager().registerEvents(new ComputersEvents(),this);

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
            ShapedRecipe recipe = new ShapedRecipe(key, Heads.CAMERA.get());
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

    public void openMenu(Player p, CCTVMenu menu) {
        Listener.openedMenus.put(p,menu);
        menu.open();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arg = args.length > 0 ? args[0] : "";
        switch (arg) {
            case "camera" -> cameraCmd.onCommand(sender,args);
            case "group" -> groupCmd.onCommand(sender,args);
            case "computer" -> computerCmd.onCommand(sender,args);
            case "debug" -> debug = !debug;
            default -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&m                                        \n"
                    + "&6[CCTV] &7" + getDescription().getVersion() + "\n"
                    + " &7- &6/cctv\n"
                    + "   &8| &eDefault help page\n"
                    + " &7- &6/cctv camera\n"
                    + "   &8| &eCamera commands\n"
                    + " &7- &6/cctv group\n"
                    + "   &8| &eCamera Group commands\n"
                    + " &7- &6/cctv computer\n"
                    + "   &8| &eComputer commands\n"
                    + "&6&m                                        "));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String arg = args.length > 1 ? args[0] : "";
        return switch (arg) {
            case "camera" -> cameraCmd.onTabComplete(sender,args);
            case "group" -> groupCmd.onTabComplete(sender,args);
            case "computer" -> computerCmd.onTabComplete(sender,args);
            default -> List.of("camera","group","computer");
        };
    }
}
