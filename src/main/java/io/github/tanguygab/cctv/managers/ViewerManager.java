package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.entities.*;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.NMSUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ViewerManager extends Manager<Viewer> {

    private final NamespacedKey VIEWER_ITEM = new NamespacedKey(cctv, "viewer-item");

    public boolean CAN_CHAT;
    public boolean ZOOM_ITEM;
    public boolean SHOW_IN_TABLIST;
    public boolean SPOTTING;
    public boolean BOSSBAR;
    private final Map<String, ViewerItem> items = new HashMap<>();

    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;
    public final List<String> blockedCommands = new ArrayList<>();

    private final CameraManager cm = cctv.getCameras();
    public final Map<UUID, Location> viewersQuit = new HashMap<>();

    public ViewerManager() {
        super("userdata.yml");
    }

    @Override
    public void load() {
        ConfigurationFile config = cctv.getConfiguration();
        CAN_CHAT = config.getBoolean("viewers.can_chat",true);
        ZOOM_ITEM = config.getBoolean("viewers.zoom_item",true);
        SHOW_IN_TABLIST = config.getBoolean("viewers.show-in-tablist",true);

        if (SHOW_IN_TABLIST && !cctv.getNms().showInTablist) {
            cctv.getLogger().warning("This server version doesn't support the show-in-tablist setting. Players will be hidden when viewing a camera");
            SHOW_IN_TABLIST = false;
        }

        Map<String, Map<String, Object>> items = config.getConfigurationSection("viewers.items");
        items.forEach((name, item) -> {
            int slot = (int) item.getOrDefault("slot", -1);
            if (slot < 0) {
                cctv.getLogger().warning("Invalid slot \"" + slot + "\" at viewers.items." + name + ".slot");
                return;
            }

            String displayName = cctv.getLang().getCameraViewItem(name);
            String material = (String) item.get("material");

            ItemStack itemStack;
            if (material.startsWith("head-")) {
                itemStack = Heads.createSkull(material.substring(5), displayName);
            } else {
                Material mat = Material.getMaterial(material);
                if (mat == null) {
                    cctv.getLogger().warning("Invalid material \"" + material + "\" at viewers.items." + name + ".material");
                    return;
                }
                itemStack = CCTVMenu.getItem(mat, displayName);
            }

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(VIEWER_ITEM, PersistentDataType.STRING, name);
                itemStack.setItemMeta(meta);
            }


            boolean onlyShowWhenGroup = (boolean) item.getOrDefault("only-show-when-group", false);
            @SuppressWarnings("unchecked") List<String> commands = (List<String>) item.getOrDefault("commands", List.of());

            this.items.put(name, new ViewerItem(slot, itemStack, onlyShowWhenGroup, commands));
        });

        SPOTTING = config.getBoolean("viewers.spotting",true);
        TIME_TO_CONNECT = config.getInt("viewers.timed-actions.connect",3);
        TIME_TO_DISCONNECT = config.getInt("viewers.timed-actions.disconnect",3);
        TIME_FOR_SPOT = config.getInt("viewers.timed-actions.spot",5);
        BOSSBAR = config.getBoolean("viewers.bossbar",true);
        blockedCommands.addAll(config.getStringList("viewers.blocked-commands",List.of()));

        Map<String,Object> loggedOutViewers = file.getConfigurationSection("logged-out-viewers");
        loggedOutViewers.keySet().forEach(this::loadFromConfig);
        file.set("logged-out-viewers",null);
    }

    @Override
    public void unload() {
        values().forEach(v-> cctv.getCameras().disconnectFromCamera(v.getPlayer()));
        viewersQuit.forEach((uuid,loc)->{
            Map<String,Object> locMap = new HashMap<>();
            locMap.put("world",loc.getWorld() != null ? loc.getWorld().getName() : "");
            locMap.put("x",loc.getX());
            locMap.put("y",loc.getY());
            locMap.put("z",loc.getZ());
            locMap.put("pitch",loc.getPitch());
            locMap.put("yaw",loc.getYaw());
            file.set("logged-out-viewers."+uuid,locMap);
        });
        items.clear();
    }

    @Override
    protected void loadFromConfig(String uuid) {
        viewersQuit.put(UUID.fromString(uuid),Utils.loadLocation("logged-out-viewers."+uuid,file));
    }

    @Override
    protected void saveToConfig(Viewer value) {}

    public void delete(Player p) {
        Viewer viewer = get(p);
        viewer.setCamera(null,false);
        if (!cm.EXPERIMENTAL_VIEW)
            for (Player online : Bukkit.getOnlinePlayers())
                online.showPlayer(cctv,p);

        p.removePotionEffect(NMSUtils.SLOWNESS);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.setCanPickupItems(true);
        p.showEntity(cctv, viewer.getCamera().getArmorStand());
        remove(viewer.getUuid().toString());
    }
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }

    public boolean exists(Player p) {
        return exists(p.getUniqueId().toString());
    }

    public void createPlayer(Player p, Computable camera, Computer computer) {
        Viewer viewer = new Viewer(p,camera,computer);
        put(viewer.getUuid().toString(),viewer);

        p.setCanPickupItems(false);
        giveViewerItems(p,computer);

        if (!cm.EXPERIMENTAL_VIEW) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p == online) continue;
                boolean canSee = online.canSee(p);
                online.hidePlayer(cctv, p);
                if (canSee && SHOW_IN_TABLIST) cctv.getNms().showViewerInTablistFor(p, online);
            }
        }
    }

    private void giveViewerItems(Player player, Computer computer) {
        player.getInventory().clear();

        boolean isGroup = computer != null && (computer.getCameras().size() > 1 || computer.getCameras().get(0) instanceof CameraGroup);
        items.forEach((name, item) -> item.giveItem(player, isGroup));
    }

    public void onCameraItems(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) return;
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (!data.has(VIEWER_ITEM, PersistentDataType.STRING)) return;

        String itemName = data.get(VIEWER_ITEM, PersistentDataType.STRING);
        if (!items.containsKey(itemName)) return;

        items.get(itemName).runCommands(player);
    }

    public void switchCamera(Player player, boolean previous) {
        if (!player.hasPermission("cctv.view.switch")) {
            player.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(player);
        Computer computer = viewer.getComputer();
        if (computer == null) {
            player.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
            return;
        }

        List<Computable> cams = new ArrayList<>(computer.getCameras());
        if (cams.isEmpty() || cams.size() == 1 && cams.get(0) instanceof Camera) {
            player.sendMessage(lang.NO_CAMERAS);
            return;
        }

        Computable group = viewer.getGroup();
        Camera camera = viewer.getCamera();
        int tries = 0;
        do {
            tries++;
            group = getNextCamera(camera,group,previous,cams);
            camera = group.get(camera);
        } while (!group.available(camera,previous) && tries < 10);

        if (group.get(camera,previous) == null) return;
        viewer.setCamera(group,previous);
    }

    public Computable getNextCamera(Camera camera, Computable current, boolean previous, List<Computable> cameras) {
        if (!current.next(camera,previous)) return current;
        int index = cameras.indexOf(current);
        if (previous) {
            if (index == 0) index = cameras.size();
            return cameras.get(--index);
        }
        if (index == (cameras.size()-1)) index = -1;
        return cameras.get(++index);

    }

}
