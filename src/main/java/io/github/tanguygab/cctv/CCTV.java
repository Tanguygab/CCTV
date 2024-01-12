package io.github.tanguygab.cctv;

import io.github.tanguygab.cctv.commands.*;
import io.github.tanguygab.cctv.config.*;
import io.github.tanguygab.cctv.listeners.*;
import io.github.tanguygab.cctv.managers.*;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.*;

import lombok.AccessLevel;
import lombok.Getter;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CCTV extends JavaPlugin {

    @Getter private static CCTV instance;

    private ConfigurationFile configuration;
    private LanguageFile lang;
    private CustomHeads customHeads;
    private NMSUtils nms;
    private CCTVExpansion expansion;

    @Getter(AccessLevel.NONE) private CameraCmd cameraCmd;
    @Getter(AccessLevel.NONE) private GroupCmd groupCmd;
    @Getter(AccessLevel.NONE) private ComputerCmd computerCmd;

    private CameraManager cameras;
    private CameraGroupManager groups;
    private ComputerManager computers;
    private ViewerManager viewers;

    @Getter(AccessLevel.NONE) private List<String> toggledCoords;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager plm = getServer().getPluginManager();
        try {
            configuration = new YamlConfigurationFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));
            String langPath = "languages/"+ configuration.getString("lang","en_US")+".yml";
            InputStream langResource = getResource(langPath);
            lang = new LanguageFile(langResource == null ? getResource("languages/en_US.yml") : langResource, new File(getDataFolder(), langPath));
            cameras = new CameraManager();
            groups = new CameraGroupManager();
            viewers = new ViewerManager();
            computers = new ComputerManager();

            toggledCoords = viewers.file.getStringList("toggled-computer-coords");
        } catch (Exception e) {
            e.printStackTrace();
            plm.disablePlugin(this);
            return;
        }

        customHeads = new CustomHeads();
        nms = new NMSUtils();

        cameraCmd = new CameraCmd();
        groupCmd = new GroupCmd();
        computerCmd = new ComputerCmd();

        viewers.load();
        cameras.load();
        groups.load();
        computers.load();

        loadRecipes();
        if (plm.isPluginEnabled("PlaceholderAPI")) (expansion = new CCTVExpansion(this)).register();
        plm.registerEvents(new Listener(),this);
        plm.registerEvents(new ViewersEvents(this),this);
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
        getLogger().info("   Spigot: https://www.spigotmc.org/resources/60310/");
        getLogger().info(" > To report bugs go to our discord and send us a message!");
        getLogger().info(".-----====--------+]-   -----+[====]+-----   -[+--------====-----.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (expansion != null) expansion.unregister();
        computers.unload();
        groups.unload();
        cameras.unload();
        viewers.file.set("toggled-computer-coords", toggledCoords);
        viewers.unload();
        Listener.openedMenus.forEach((p,inv)->p.closeInventory());
        getLogger().info("CCTV Plugin has been successfully Disabled!");
    }

    private void loadRecipes() {
        Map<String,String> camItems = new HashMap<>() {{
            put("R","REDSTONE_BLOCK");
            put("P","HEAVY_WEIGHTED_PRESSURE_PLATE");
            put("D","DISPENSER");
            put("G","GLASS_PANE");
            put("L","DAYLIGHT_DETECTOR");
            put("C","COMPARATOR");
        }};
        loadRecipe(cameras.cameraKey, customHeads.get(null),"camera",List.of("RPP","PDG","LCP"),camItems);

        Map<String,String> computerItems = new HashMap<>() {{
            put("I","REDSTONE");
            put("P","HEAVY_WEIGHTED_PRESSURE_PLATE");
            put("G","GLASS_PANE");
            put("C","COMPARATOR");
            put("R","REPEATER");
            put("T","REDSTONE_TORCH");
        }};
        loadRecipe(computers.computerKey,computers.COMPUTER_ITEM.clone(),"computer",List.of("ITI","PGP","RCR"),computerItems);
    }
    private void loadRecipe(NamespacedKey key, ItemStack item, String cfg, List<String> defShape, Map<String,String> defItems) {
        if (getServer().getRecipe(key) != null) getServer().removeRecipe(key);
        if (!configuration.getBoolean(cfg+".craft.enabled",true)) return;
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        List<String> shape = configuration.getStringList(cfg+".craft.shape",defShape);
        recipe.shape(shape.toArray(new String[]{}));
        Map<String,String> items = configuration.getConfigurationSection(cfg+".craft.items");
        if (items.isEmpty()) {
            items = defItems;
            configuration.set(cfg+".craft.items",items);
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

    public void toggleComputerCoords(Player player) {
        String uuid = player.getUniqueId().toString();
        if (toggledCoords.contains(uuid))
            toggledCoords.remove(uuid);
        else toggledCoords.add(uuid);
    }

    public boolean hasToggledComputerCoords(Player player) {
        return toggledCoords.contains(player.getUniqueId().toString());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String arg = args.length > 0 ? args[0] : "";
        switch (arg) {
            case "camera" -> cameraCmd.onCommand(sender,args);
            case "computer" -> computerCmd.onCommand(sender,args);
            case "group" -> groupCmd.onCommand(sender,args);
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
                    + "   &8| &eGroup commands\n"
                    + " &7- &6/cctv computer\n"
                    + "   &8| &eComputer commands\n"
                    + " &7- &6/cctv reload\n"
                    + "   &8| &eReload the plugin\n"
                    + "&6&m                                        "));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String arg = args.length > 1 ? args[0] : "";
        return switch (arg) {
            case "camera" -> cameraCmd.onTabComplete(sender,args);
            case "group" -> groupCmd.onTabComplete(sender,args);
            case "computer" -> computerCmd.onTabComplete(sender,args);
            default -> List.of("camera","group","computer","reload");
        };
    }
}
