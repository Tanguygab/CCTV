package io.github.tanguygab.cctv;

import io.github.tanguygab.cctv.api.CCTVAPI;
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
import io.github.tanguygab.cctv.utils.NMSUtils;
import io.github.tanguygab.cctv.utils.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCTV extends JavaPlugin {

    private static CCTV instance;
    public static CCTV get() {
        return instance;
    }

    private ConfigurationFile config;
    private LanguageFile lang;
    private CustomHeads customHeads;
    private NMSUtils nms;
    public ConfigurationFile getConfiguration() {
        return config;
    }
    public LanguageFile getLang() {
        return lang;
    }
    public CustomHeads getCustomHeads() {
        return customHeads;
    }
    public NMSUtils getNMS() {
        return nms;
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
        new CCTVAPI(this);
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
        nms = new NMSUtils();

        cameraCmd = new CameraCmd();
        groupCmd = new GroupCmd();
        computerCmd = new ComputerCmd();

        cameraManager.load();
        cameraGroupManager.load();
        computerManager.load();
        viewerManager.load();

        loadRecipes();
        PluginManager plm = getServer().getPluginManager();
        plm.registerEvents(new Listener(),this);
        plm.registerEvents(new ViewersEvents(),this);
        plm.registerEvents(new ComputersEvents(),this);

        getLogger().info(".-----====--------+]-   -----+[====]+-----   -[+--------====-----.");
        getLogger().info("");
        getLogger().info("   _____ _____ _________      __  _____  _             _        ");
        getLogger().info("  / ____/ ____|__   __\\ \\    / / |  __ \\| |           (_)      ");
        getLogger().info(" | |   | |       | |   \\ \\  / /  | |__) | |_   _  __ _ _ _ __  ");
        getLogger().info(" | |   | |       | |    \\ \\/ /   |  ___/| | | | |/ _` | |  _ \\ ");
        getLogger().info(" | |___| |____   | |     \\  /    | |    | | |_| | (_| | | | | |");
        getLogger().info("  \\_____\\_____|  |_|      \\/     |_|    |_|\\__,_|\\__, |_|_| |_|");
        getLogger().info("                                                  __/ |        ");
        getLogger().info("                                                 |___/         ");
        getLogger().info(".-----====--------+]-   -----+[====]+-----   -[+--------====-----.");
        getLogger().info(" > Thank you for downloading the CCTV Plugin!!");
        getLogger().info(" > Authors: Timdecoole123, Streampy, Tanguygab");
        getLogger().info(" > For updates check our spigot page");
        getLogger().info("   Spigot: https://www.spigotmc.org/resources/cctv.60310/");
        getLogger().info(" > To report bugs go to our discord and send us a message!");
        getLogger().info(".-----====--------+]-   -----+[====]+-----   -[+--------====-----.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        cameraManager.unload();
        viewerManager.unload();
        Listener.openedMenus.forEach((p,inv)->p.closeInventory());
        getLogger().info("CCTV Plugin has been successfully Disabled!");
    }

    private void loadRecipes() {
        Map<String,String> camItems = new HashMap<>();
        camItems.put("R","REDSTONE_BLOCK");
        camItems.put("P","HEAVY_WEIGHTED_PRESSURE_PLATE");
        camItems.put("D","DISPENSER");
        camItems.put("G","GLASS_PANE");
        camItems.put("L","DAYLIGHT_DETECTOR");
        camItems.put("C","COMPARATOR");
        loadRecipe(Utils.cameraKey, Heads.CAMERA.get(),"camera",List.of("RPP","PDG","LCP"),camItems);

        Map<String,String> computerItems = new HashMap<>();
        computerItems.put("I","REDSTONE");
        computerItems.put("P","HEAVY_WEIGHTED_PRESSURE_PLATE");
        computerItems.put("G","GLASS_PANE");
        computerItems.put("C","COMPARATOR");
        computerItems.put("R","REPEATER");
        computerItems.put("T","REDSTONE_TORCH");
        loadRecipe(Utils.computerKey,ComputerManager.COMPUTER_ITEM.clone(),"computer",List.of("ITI","PGP","RCR"),computerItems);
    }
    private void loadRecipe(NamespacedKey key, ItemStack item, String cfg, List<String> defShape, Map<String,String> defItems) {
        if (getServer().getRecipe(key) != null) getServer().removeRecipe(key);
        if (!config.getBoolean(cfg+".craft.enabled",true)) return;
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        List<String> shape = config.getStringList(cfg+".craft.shape",defShape);
        recipe.shape(shape.toArray(new String[]{}));
        Map<String,String> items = config.getConfigurationSection(cfg+".craft.items");
        if (items.isEmpty()) {
            items = defItems;
            config.set(cfg+".craft.items",items);
        }
        items.forEach((c,mat)->{
            Material material = Material.getMaterial(mat);
            if (material == null) {
                getLogger().info("Invalid item material at "+cfg+".craft.items."+c+": \""+mat+"\"");
                return;
            }
            recipe.setIngredient(c.charAt(0), material);
        });
        getServer().addRecipe(recipe);

    }

    public void openMenu(Player p, CCTVMenu menu) {
        Listener.openedMenus.put(p,menu);
        menu.open();
    }

    private boolean cam = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arg = args.length > 0 ? args[0] : "";
        switch (arg) {
            case "camera" -> cameraCmd.onCommand(sender,args);
            case "group" -> groupCmd.onCommand(sender,args);
            case "computer" -> computerCmd.onCommand(sender,args);
            case "reload" -> {
                if (!sender.hasPermission("cctv.reload")) {
                    sender.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                }
                onDisable();
                onEnable();
                sender.sendMessage("Plugin reloaded!");
            }
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
                    + " &7- &6/cctv reload\n"
                    + "   &8| &eReload the plugin\n"
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
            default -> List.of("camera","group","computer","reload");
        };
    }
}
