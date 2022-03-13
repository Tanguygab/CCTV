package io.github.tanguygab.cctv.managers;

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
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ViewerManager extends Manager<Viewer> {

    private boolean CISWP;
    public int TIME_TO_CONNECT;
    public int TIME_TO_DISCONNECT;
    public int TIME_FOR_SPOT;

    private final CameraManager cm = cctv.getCameras();

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
        return exists(p.getUniqueId().toString());
    }

    private static void playerSetMode(Player p, boolean mode, GameMode gm) {
        p.setCanPickupItems(!mode);
        p.setGameMode(gm);
        if (gm != GameMode.CREATIVE && gm != GameMode.SPECTATOR) {
            p.setAllowFlight(mode);
            p.setFlying(mode);
        }
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

    private boolean hasItemPerm(Player p, String perm) {
        return CISWP || p.hasPermission("cctv.view."+perm);
    }

    private void giveViewerItems(Player p, CameraGroup group) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (CISWP || p.hasPermission("cctv.view.zoom") || p.hasPermission("cctv.view.nightvision") || p.hasPermission("cctv.view.spot"))
            inv.setItem(0, Utils.getItem(Heads.OPTIONS,lang.CAMERA_VIEW_OPTION));
        if (hasItemPerm(p,"move")) {
            inv.setItem(3, Heads.ROTATE_LEFT.get());
            inv.setItem(group != null && group.getCameras().size() > 1 ? 4 : 5, Heads.ROTATE_RIGHT.get());
        }
        if (hasItemPerm(p,"switch") && group != null && group.getCameras().size() > 1) {
            inv.setItem(6, Heads.CAM_PREVIOUS.get());
            inv.setItem(7, Heads.CAM_NEXT.get());
        }
        inv.setItem(8, Utils.getItem(Heads.EXIT,lang.CAMERA_VIEW_EXIT));
    }

    public void onCameraItems(Player p, String item) {
        if (item.equals(lang.CAMERA_VIEW_EXIT)) {
            p.sendTitle(" ", cctv.getLang().CAMERA_DISCONNECTING, 0, TIME_TO_DISCONNECT*20, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> cm.unviewCamera(p),  TIME_TO_DISCONNECT * 20L);
            return;
        }
        if (item.equals(lang.CAMERA_VIEW_OPTION)) openOptions(p);
        if (item.equals(lang.CAMERA_VIEW_ROTATE_LEFT)) rotateCamera(p, -18);
        if (item.equals(lang.CAMERA_VIEW_ROTATE_RIGHT)) rotateCamera(p, 18);
        if (item.equals(lang.CAMERA_VIEW_PREVIOUS)) switchCamera(p,true);
        if (item.equals(lang.CAMERA_VIEW_NEXT)) switchCamera(p,false);
    }

    public void onCameraOptionsMenu(Player viewer, String item) {
        if (item.matches(lang.getCameraViewZoom(-1))) {
            String zoom = lang.getMatcher(lang.getCameraViewZoom(-1),item,"camera-view.zoom","%level%");
            if (zoom == null) return;
            int x = Integer.parseInt(zoom);
            zoom(viewer, (x == 6) ? 0 : (x + 1));
        }
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_SPOT)) spotting(viewer);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF)) nightvision(viewer, true);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_NIGHTVISION_ON)) nightvision(viewer, false);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF)) zoom(viewer, 1);
        if (item.equals(lang.CAMERA_VIEW_OPTIONS_BACK))  viewer.closeInventory();
    }

    private void openOptions(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, lang.GUI_CAMERA_SETTINGS);
        if (hasItemPerm(p,"nightvision")) inv.setItem(3, p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? Heads.NIGHT_VISION_ON.get() : Heads.NIGHT_VISION_OFF.get());

        if (hasItemPerm(p,"zoom")) {
            PotionEffect effect = p.getPotionEffect(PotionEffectType.SLOW);
            inv.setItem(4, Utils.getItem(Heads.ZOOM,
                    effect != null
                            ? lang.getCameraViewZoom(effect.getAmplifier()+1)
                            : lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF
            ));
        }

        if (hasItemPerm(p,"spot")) inv.setItem(5, Utils.getItem(Heads.SPOTTING,lang.CAMERA_VIEW_OPTIONS_SPOT));

        inv.setItem(8, Utils.getItem(Heads.EXIT,lang.CAMERA_VIEW_OPTIONS_BACK));
        p.openInventory(inv);
    }

    private boolean spot(Player viewer, Player viewed, boolean glow) {
        if (!viewed.canSee(viewed)) return false;
        if (!glow) {
            viewed.setSneaking(true); // yeah, I'm doing that because it doesn't want to work with PacketPlayOutEntityMetadata...
            viewed.setSneaking(false);
            return true;
        }
        EntityPlayer viewedNMS = ((CraftPlayer)viewed).getHandle();
        viewedNMS.i(true); //setGlowingTag(boolean)
        PlayerConnection connection = ((CraftPlayer)viewer).getHandle().b;
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(viewedNMS.ae(),viewedNMS.ai(),true);
        connection.a(packet);
        return true;
    }

    private void spotting(Player p) {
        if (!p.hasPermission("cctv.view.spot")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        p.closeInventory();
        List<Player> spotted = new ArrayList<>();
        for (Player viewed : Bukkit.getOnlinePlayers())
            if (spot(p, viewed,true))
                spotted.add(viewed);

        Bukkit.getScheduler().scheduleSyncDelayedTask(cctv, () -> spotted.forEach(viewed->spot(viewed,viewed,false)), cctv.getViewers().TIME_FOR_SPOT * 20L);
    }
    private void nightvision(Player p, boolean vision) {
        if (!p.hasPermission("cctv.view.nightvision")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (vision) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60000000, 0, false, false));
            inv.setItem(3, Heads.NIGHT_VISION_ON.get());
            return;
        }
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        inv.setItem(3, Heads.NIGHT_VISION_OFF.get());
    }
    private void rotateCamera(Player p, int degrees) {
        if (!p.hasPermission("cctv.view.move")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(p);
        Location loc = viewer.getCamera().getArmorStand().getLocation();
        float yaw = Math.round(loc.getYaw() + degrees);
        Camera cam = viewer.getCamera();
        float camYaw = cam.getLocation().getYaw();
        if (yaw >= Math.round(((camYaw > 359.0F) ? (camYaw - 360.0F) : camYaw) - 36.0F) && yaw <= Math.round(((camYaw > 359.0F) ? (camYaw - 360.0F) : camYaw) + 36.0F)) {
            loc.setYaw(yaw);
            cam.getArmorStand().teleport(loc);
            for (Viewer otherViewer : values())
                if (otherViewer.getCamera() == viewer.getCamera())
                    cm.teleport(viewer.getCamera(), get(otherViewer));
        } else p.sendMessage(lang.MAX_ROTATION);

    }
    private void switchCamera(final Player p, boolean previous) {
        if (!p.hasPermission("cctv.view.switch")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Viewer viewer = get(p);
        CameraGroup group = viewer.getGroup();
        if (group == null) {
            p.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
            return;
        }
        if (group.getCameras().size() <= 1) {
            p.sendMessage(lang.NO_CAMERAS);
            return;
        }

        List<Camera> cams = new ArrayList<>(group.getCameras());
        if (previous) Collections.reverse(cams);

        Camera currentCam = viewer.getCamera();
        Camera cam = cams.indexOf(currentCam) == cams.size()-1
                ? cams.get(0)
                : cams.get(cams.indexOf(currentCam)+1);
        cm.viewCameraInstant(cam, p);
        viewer.setCamera(cam);
    }
    private void zoom(Player p, int zoomlevel) {
        if (!p.hasPermission("cctv.view.zoom")) {
            p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (zoomlevel == 0) {
            p.removePotionEffect(PotionEffectType.SLOW);
            inv.setItem(4, Heads.ZOOM.get());
            return;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60000000, zoomlevel - 1, false, false));
        inv.setItem(4, Utils.getItem(Heads.ZOOM,lang.getCameraViewZoom(zoomlevel)));
    }


}
