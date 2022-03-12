package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewerManager extends Manager<Viewer> {

    private boolean CISWP;
    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;

    public ViewerManager() {
        super();
    }

    @Override
    public void load() {
        ConfigurationFile config = cctv.getConfiguration();
        CISWP = config.getBoolean("camera_inventory_show_item_without_permissions");
        TIME_TO_CONNECT = config.getInt("time_to_connect",3);
        TIME_TO_DISCONNECT = config.getInt("time_to_disconnect",3);
        TIME_FOR_SPOT = config.getInt("time_for_spot",5);
    }

    @Override
    public void unload() {}

    @Override
    public void delete(String id, Player player) {
        Viewer viewer = get(id);
        if (viewer == null) {
            if (player != null) player.sendMessage("This player isn't viewing a camera!");
            return;
        }
        Player p = get(viewer);
        p.getInventory().setContents(viewer.getInv());

        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.removePotionEffect(PotionEffectType.SLOW);
        playerSetMode(p,false, viewer.getGameMode());
        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(cctv,p);
        p.teleport(viewer.getLoc());
        map.remove(id);
    }

    public void delete(Player p) {delete(p.getUniqueId().toString(),null);}
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }
    public Player get(Viewer viewer) {
        return Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
    }

    public boolean exists(Player p) {
        return super.exists(p.getUniqueId().toString());
    }

    private static void playerSetMode(Player p, boolean mode, GameMode gm) {
        p.setCanPickupItems(!mode);
        p.setAllowFlight(mode);
        p.setGameMode(gm);
        if (gm != GameMode.CREATIVE && gm != GameMode.SURVIVAL)
            p.setFlying(mode);
        p.setCollidable(!mode);
        p.setInvulnerable(mode);
    }

    public void createPlayer(Player p, Camera cam, CameraGroup group) {
        Viewer viewer = new Viewer(p,cam,group);
        map.put(viewer.getId(),viewer);

        playerSetMode(p,true, GameMode.ADVENTURE);
        giveViewerItems(p,group);

        for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(cctv,p);
    }

    private void giveViewerItems(Player p, CameraGroup group) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (CISWP || p.hasPermission("cctv.view.zoom") || p.hasPermission("cctv.view.nightvision") || p.hasPermission("cctv.view.spot"))
            inv.setItem(0, Utils.getItem(Heads.OPTIONS,lang.CAMERA_VIEW_OPTION));
        if (CISWP || p.hasPermission("cctv.view.move")) {
            inv.setItem(3, Heads.ROTATE_LEFT.get());
            inv.setItem(group != null && group.getCameras().size() > 1 ? 4 : 5, Heads.ROTATE_RIGHT.get());
        }
        if ((CISWP || p.hasPermission("cctv.view.switch")) && group != null && group.getCameras().size() > 1) {
            inv.setItem(6, Heads.CAM_PREVIOUS.get());
            inv.setItem(7, Heads.CAM_NEXT.get());
        }
        inv.setItem(8, Utils.getItem(Heads.EXIT,lang.CAMERA_VIEW_EXIT));
    }

    public void switchFunction(Player p, String item) {
        if (item.equals(lang.CAMERA_VIEW_OPTION)) {
            Inventory inv = Bukkit.createInventory(null, 9, lang.GUI_CAMERA_SETTINGS);
            if (CISWP || p.hasPermission("cctv.view.nightvision"))
                inv.setItem(3, p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());

            if (CISWP || p.hasPermission("cctv.view.zoom")) {
                PotionEffect effect = p.getPotionEffect(PotionEffectType.SLOW);
                inv.setItem(4, Utils.getItem(Heads.ZOOM,
                        effect != null
                                ? lang.getCameraViewZoom(effect.getAmplifier()+1)
                                : lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF
                ));
            }
            if (CISWP || p.hasPermission("cctv.view.spot"))
                inv.setItem(5, Utils.getItem(Heads.SPOTTING,lang.CAMERA_VIEW_OPTIONS_SPOT));
            
            inv.setItem(8, Utils.getItem(Heads.EXIT,lang.CAMERA_VIEW_OPTIONS_BACK));
            p.openInventory(inv);
            return;
        }
        if (item.equals(lang.CAMERA_VIEW_EXIT)) {
            p.sendTitle("", cctv.getLang().CAMERA_DISCONNECTING, 0, 15, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> cctv.getCameras().unviewCamera(p),  TIME_TO_DISCONNECT * 20L);
            return;
        }
        if (item.equals(lang.CAMERA_VIEW_ROTATE_LEFT)) rotateCamera(p, -18);
        if (item.equals(lang.CAMERA_VIEW_ROTATE_RIGHT)) rotateCamera(p, 18);
        if (item.equals(lang.CAMERA_VIEW_PREVIOUS)) previousCamera(p);
        if (item.equals(lang.CAMERA_VIEW_NEXT)) nextCamera(p);
    }

    public void settingFunction(Player viewer, String item) {
        if (item.matches(lang.getCameraViewZoom(-1))) {
            String zoom = lang.getMatcher(lang.getCameraViewZoom(-1),item,"camera-view.zoom","%level%");
            if (zoom == null) return;
            int x = Integer.parseInt(zoom);
            zoom(viewer, (x == 6) ? 0 : (x + 1));
        }
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_SPOT)) {
            viewer.closeInventory();
            List<Player> spotted = new ArrayList<>();
            for (Player viewed : Bukkit.getOnlinePlayers())
                if (spot(viewer, viewed,true))
                    spotted.add(viewed);

            Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> {
                spotted.forEach(viewed->spot(viewed,viewed,false));
            }, cctv.getViewers().TIME_FOR_SPOT * 20L);

        }
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF)) nightvision(viewer, true);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_NIGHTVISION_ON)) nightvision(viewer, false);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF)) zoom(viewer, 1);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_BACK))  viewer.closeInventory();
    }

    private boolean spot(Player viewer, Player viewed, boolean glow) {
        if (!viewed.canSee(viewed)) return false;
        EntityPlayer viewedNMS = ((CraftPlayer)viewed).getHandle();
        PlayerConnection connection = ((CraftPlayer)viewer).getHandle().b;

        viewedNMS.i(glow); //setGlowingTag(boolean)
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(viewedNMS.ae(),viewedNMS.ai(),true);
        connection.a(packet);
        return true;
    }

    public void nightvision(Player player, boolean b) {

    }
    public void rotateCamera(Player player, int degrees) {}
    public void previousCamera(final Player player) {}
    public void nextCamera(final Player player) {}
    public void zoom(Player player, int zoomlevel) {}


}
