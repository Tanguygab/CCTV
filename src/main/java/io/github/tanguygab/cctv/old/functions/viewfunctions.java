package io.github.tanguygab.cctv.old.functions;

import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class viewfunctions {
  
  public static void nightvision(Player player, boolean b) {
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.nightvision")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    } 
    if (b) {
      PotionEffect nightvision = new PotionEffect(PotionEffectType.NIGHT_VISION, 60000000, 0, false, false);
      player.addPotionEffect(nightvision);
      player.getOpenInventory().getTopInventory().setItem(3, Heads.NIGHT_VISION_ON.get());
    } else {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.getOpenInventory().getTopInventory().setItem(3, Heads.NIGHT_VISION_OFF.get());
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
          Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(), () -> CCTV.get().getCameras().teleport(viewer.getCamera(), p),0L);
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
    LanguageFile lang = CCTV.get().getLang();
    if (!player.hasPermission("cctv.view.zoom")) {
      player.sendMessage(lang.NO_PERMISSIONS);
      return;
    } 
    if (zoomlevel == 0) {
      player.removePotionEffect(PotionEffectType.SLOW);
      ItemStack zoom = Heads.ZOOM.get();
      ItemMeta zoomM = zoom.getItemMeta();
      zoomM.setDisplayName(lang.CAMERA_VIEW_OPTIONS_ZOOM_OFF);
      zoom.setItemMeta(zoomM);
      player.getOpenInventory().getTopInventory().setItem(4, zoom);
    } else {
      PotionEffect zoom = new PotionEffect(PotionEffectType.SLOW, 60000000, zoomlevel - 1, false, false);
      player.addPotionEffect(zoom);
      ItemStack zoomI = Heads.ZOOM.get();
      ItemMeta zoomM = zoomI.getItemMeta();
      zoomM.setDisplayName(lang.getCameraViewZoom(zoomlevel));
      zoomI.setItemMeta(zoomM);
      player.getOpenInventory().getTopInventory().setItem(4, zoomI);
    } 
  }
}
