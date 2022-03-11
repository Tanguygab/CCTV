package io.github.tanguygab.cctv.old.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.old.library.Reflect;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class viewfunctions {
  public static void switchFunctions(final Player player, ItemStack item) {
    LanguageFile lang = CCTV.get().getLang();
    if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_rotate_left)) {
      move(player, -18);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_rotate_right)) {
      move(player, 18);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_group_prev)) {
      switchLeft(player);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_group_next)) {
      switchRight(player);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_option)) {
      Inventory inv = Bukkit.createInventory(null, 9, lang.GUI_CAMERA_SETTINGS);
      if (CCTV.get().CISIWP || player.hasPermission("cctv.view.nightvision")) {
        ItemStack nightvision = (player.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? Heads.NIGHT_VISION_ON : Heads.NIGHT_VISION_OFF).get();
        ItemMeta nightvisionM = nightvision.getItemMeta();
        nightvisionM.setDisplayName(player.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? Arguments.item_camera_view_options_nightvision_on : Arguments.item_camera_view_options_nightvision_off);
        nightvision.setItemMeta(nightvisionM);
        inv.setItem(3, nightvision);
      } 
      if (CCTV.get().CISIWP || player.hasPermission("cctv.view.zoom")) {
        ItemStack zoom = Heads.PLUS.get();
        ItemMeta zoomM = zoom.getItemMeta();
        zoomM.setDisplayName(player.hasPotionEffect(PotionEffectType.SLOW) ? Arguments.item_camera_view_options_zoom.replaceAll("%level%", (player.getPotionEffect(PotionEffectType.SLOW).getAmplifier()+1)+"") : Arguments.item_camera_view_options_zoom_off);
        zoom.setItemMeta(zoomM);
        inv.setItem(4, zoom);
      } 
      if (CCTV.get().CISIWP || player.hasPermission("cctv.view.spot")) {
        ItemStack spot = Heads.SPOTTING.get();
        ItemMeta spotM = spot.getItemMeta();
        spotM.setDisplayName(Arguments.item_camera_view_options_spot);
        spot.setItemMeta(spotM);
        inv.setItem(5, spot);
      } 
      ItemStack back = Heads.EXIT.get();
      ItemMeta backM = back.getItemMeta();
      backM.setDisplayName(Arguments.item_camera_view_options_back);
      back.setItemMeta(backM);
      inv.setItem(8, back);
      player.openInventory(inv);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_exit)) {
      player.sendTitle("", CCTV.get().getLang().CAMERA_DISCONNECTING, 0, 15, 0);
      Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> CCTV.get().getCameras().unviewCamera(player),  CCTV.get().TIME_TO_DISCONNECT * 20L);
    } 
  }
  
  public static void settingFunctions(final Player player, ItemStack item) {
    if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_options_nightvision_off)) {
      nightvision(player, true);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_options_nightvision_on)) {
      nightvision(player, false);
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_options_zoom_off)) {
      zoom(player, 1);
    } else if (item.getItemMeta().getDisplayName().matches(Arguments.item_camera_view_options_zoom.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%level%", "*\\\\d+"))) {
      Pattern p = Pattern.compile(Arguments.item_camera_view_options_zoom.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%level%", "*\\(\\\\d+\\)"));
      Matcher m = p.matcher(item.getItemMeta().getDisplayName());
      if (!m.matches()) {
        Bukkit.getLogger().info("The item of 'item_camera_view_options_zoom' doesn't contain %zoom%!");
        return;
      } 
      int x = Integer.parseInt(m.group(1));
      zoom(player, (x == 6) ? 0 : (x + 1));
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_options_back)) {
      player.closeInventory();
    } else if (item.getItemMeta().getDisplayName().equals(Arguments.item_camera_view_options_spot)) {
      player.closeInventory();
      try {
        final Method sendPacket = Reflect.getNMSClass("PlayerConnection").getMethod("sendPacket", Reflect.getNMSClass("Packet"));
        final ArrayList<Player> spot = new ArrayList<>();
        for (Player o : Bukkit.getOnlinePlayers()) {
          if (player.canSee(o) && player != o) {
            Object playerNMS = Reflect.getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle").invoke(o);
            Class<?> dataWatcherClass = Reflect.getNMSClass("DataWatcher");
            Method setFlag = playerNMS.getClass().getMethod("setFlag", int.class, boolean.class);
            setFlag.invoke(playerNMS, 6, true);
            playerNMS.getClass().getField("glowing").set(playerNMS, true);
            Constructor<?> PacketPlayOutEntityMetadataConstructor = Reflect.getNMSClass("PacketPlayOutEntityMetadata").getConstructor(int.class, dataWatcherClass, boolean.class);
            Object PacketPlayOutEntityMetadata = PacketPlayOutEntityMetadataConstructor.newInstance(playerNMS.getClass().getMethod("getId").invoke(playerNMS),
                    playerNMS.getClass().getMethod("getDataWatcher").invoke(playerNMS),
                    true);
            sendPacket.invoke(Reflect.getConnection(player), PacketPlayOutEntityMetadata);
            spot.add(o);
          } 
        } 
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> {
          try {
            for (Player o : spot) {
              Object playerNMS = Reflect.getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle").invoke(o);
              Class<?> dataWatcherClass = Reflect.getNMSClass("DataWatcher");
              playerNMS.getClass().getField("glowing").set(playerNMS, false);
              Method setFlag = playerNMS.getClass().getMethod("setFlag", int.class, boolean.class);
              setFlag.invoke(playerNMS, 6, false);

              Constructor<?> PacketPlayOutEntityMetadataConstructor = Reflect.getNMSClass("PacketPlayOutEntityMetadata").getConstructor(int.class, dataWatcherClass, boolean.class);
              Object PacketPlayOutEntityMetadata = PacketPlayOutEntityMetadataConstructor.newInstance(playerNMS.getClass().getMethod("getId").invoke(playerNMS),
                      playerNMS.getClass().getMethod("getDataWatcher").invoke(playerNMS),
                      true);
              sendPacket.invoke(Reflect.getConnection(player), PacketPlayOutEntityMetadata);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }, CCTV.get().TIME_FOR_SPOT * 20L);
      } catch (Exception ex) {
        ex.printStackTrace();
      } 
    } 
  }
  
  public static void nightvision(Player player, boolean b) {
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.nightvision")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    } 
    if (b) {
      PotionEffect nightvision = new PotionEffect(PotionEffectType.NIGHT_VISION, 60000000, 0, false, false);
      player.addPotionEffect(nightvision, true);
      ItemStack nightvisionI = Heads.NIGHT_VISION_ON.get();
      ItemMeta nightvisionM = nightvisionI.getItemMeta();
      nightvisionM.setDisplayName(Arguments.item_camera_view_options_nightvision_on);
      nightvisionI.setItemMeta(nightvisionM);
      player.getOpenInventory().getTopInventory().setItem(3, nightvisionI);
    } else {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      ItemStack nightvisionI = Heads.NIGHT_VISION_OFF.get();
      ItemMeta nightvisionM = nightvisionI.getItemMeta();
      nightvisionM.setDisplayName(Arguments.item_camera_view_options_nightvision_off);
      nightvisionI.setItemMeta(nightvisionM);
      player.getOpenInventory().getTopInventory().setItem(3, nightvisionI);
    } 
  }
  
  public static void move(Player player, int degrees) {
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.move")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    }
    ViewerManager vm = CCTV.get().getViewers();
    Viewer viewer = vm.get(player);
    Location loc = viewer.getCamera().getArmorStand().getLocation();
    float yaw = Math.round(loc.getYaw() + degrees);
    Camera cam = viewer.getCamera();
    float camYaw = cam.getLocation().getYaw();
    if (yaw >= Math.round(((camYaw > 359.0F) ? (camYaw - 360.0F) : camYaw) - 36.0F) && yaw <= Math.round(((camYaw > 359.0F) ? (camYaw - 360.0F) : camYaw) + 36.0F)) {
      loc.setYaw(yaw);
      cam.getArmorStand().teleport(loc);
      for (Viewer viewers : vm.values()) {
        if (viewers.getCamera().getId().equals(viewer.getCamera().getId())) {
          final Player p = Bukkit.getPlayer(UUID.fromString(viewers.getId()));
          Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> camerafunctions.teleportToCamera(viewer.getCamera().getId(), p),0L);
          return;
        } 
      } 
    } else {
      player.sendMessage(lang.MAX_ROTATION);
    } 
  }
  
  public static void switchLeft(final Player player) {
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.switch")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    } 
    Viewer rec = CCTV.get().getViewers().get(player);
    CameraGroup group = rec.getGroup();
    if (group != null) {
      if (group.getCameras().size() > 1) {
        for (int i = group.getCameras().size() - 1; i >= 0; i--) {
          if (group.getCameras().get(i).getId().equalsIgnoreCase(rec.getCamera().getId())) {
            if (i != 0) {
              final Camera cam = group.getCameras().get(i - 1);
              Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> camerafunctions.immediateViewCamera(cam.getId(), player),
              0L);
              rec.setCamera(cam);
              break;
            } 
            Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> camerafunctions.immediateViewCamera(group.getCameras().get(group.getCameras().size() - 1).getId(), player),
            0L);
            rec.setCamera(group.getCameras().get(group.getCameras().size() - 1));
            i = group.getCameras().size() - 1;
            break;
          } 
        } 
      } else {
        player.sendMessage(lang.NO_CAMERAS);
      } 
    } else {
      player.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
    } 
  }
  
  public static void switchRight(final Player player) {
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.switch")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    }
    Viewer rec = CCTV.get().getViewers().get(player);
    CameraGroup group = rec.getGroup();
    if (group != null) {
      if (group.getCameras().size() > 1) {
        for (int i = 0; i < group.getCameras().size(); i++) {
          if ((group.getCameras().get(i)).getId().equalsIgnoreCase(rec.getCamera().getId())) {
            if (i != group.getCameras().size() - 1) {
              final Camera cam = group.getCameras().get(i + 1);
              Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> camerafunctions.immediateViewCamera(cam.getId(), player),0L);
              rec.setCamera(cam);
              break;
            } 
            Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> camerafunctions.immediateViewCamera(group.getCameras().get(0).getId(), player),0L);
            rec.setCamera(group.getCameras().get(0));
            i = 0;
            break;
          } 
        } 
      } else {
        player.sendMessage(lang.NO_CAMERAS);
      } 
    } else {
      player.sendMessage(lang.SWITCHING_NOT_POSSIBLE);
    } 
  }
  
  public static void zoom(Player player, int zoomlevel) {
    if (!player.hasPermission("cctv.view.zoom")) {
      player.sendMessage(Arguments.no_perms);
      return;
    } 
    if (zoomlevel == 0) {
      player.removePotionEffect(PotionEffectType.SLOW);
      ItemStack zoom = Heads.PLUS.get();
      ItemMeta zoomM = zoom.getItemMeta();
      zoomM.setDisplayName(Arguments.item_camera_view_options_zoom_off);
      zoom.setItemMeta(zoomM);
      player.getOpenInventory().getTopInventory().setItem(4, zoom);
    } else {
      PotionEffect zoom = new PotionEffect(PotionEffectType.SLOW, 60000000, zoomlevel - 1, false, false);
      player.addPotionEffect(zoom);
      ItemStack zoomI = Heads.PLUS.get();
      ItemMeta zoomM = zoomI.getItemMeta();
      zoomM.setDisplayName(Arguments.item_camera_view_options_zoom.replaceAll("%level%", zoomlevel+""));
      zoomI.setItemMeta(zoomM);
      player.getOpenInventory().getTopInventory().setItem(4, zoomI);
    } 
  }
}
