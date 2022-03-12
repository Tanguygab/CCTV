package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.old.Search;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class groupfunctions {
  
  public static void list(Player player, int page, Search s, String search) {
    LanguageFile lang = CCTV.get().getLang();
    ArrayList<CameraGroup> list = new ArrayList<>();
    CCTV.get().getCameraGroups().values().stream().filter(c -> !(s != Search.all && (s != Search.personal || !c.getOwner().equals(player.getUniqueId().toString())) && (s != Search.name || !c.getId().toLowerCase().startsWith(search.toLowerCase())) && (s != Search.player || c.getOwner().equals("none") || !Bukkit.getOfflinePlayer(UUID.fromString(c.getOwner())).getName().startsWith(search)))).forEach(g -> {

        });
    list.sort((c1, c2) -> {
      String name1 = "none";
      String name2 = "none";
      if (!c1.getOwner().equals("none")) {
        OfflinePlayer off1 = Bukkit.getOfflinePlayer(UUID.fromString(c1.getOwner()));
        name1 = off1.getName();
      }
      if (!c2.getOwner().equals("none")) {
        OfflinePlayer off2 = Bukkit.getOfflinePlayer(UUID.fromString(c2.getOwner()));
        name2 = off2.getName();
      }
      return name2.compareTo(name1);
    });
    int maxpages = (int)Math.ceil(list.size() / 8.0D);
    if (page > maxpages && page == 1) {
      player.sendMessage(lang.getListNoResult(s.toString(),search));
      return;
    } 
    if (page > maxpages || page < 1) {
      player.sendMessage(lang.TOO_MANY_PAGES);
      return;
    } 
    for (int a = (page - 1) * 8; a < 8 * page && a < list.size(); a++) {
      CameraGroup rec = list.get(a);
      String name = "none";
      if (!rec.getOwner().equals("none")) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(rec.getOwner()));
        name = off.getName();
      } 
      player.sendMessage(((s == Search.all || s == Search.player || s == Search.name) ? lang.getListAdmin(name,rec.getId()) : lang.getList(rec.getId())));
    } 
    player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + maxpages + ChatColor.YELLOW + " =====");
  }
  
  public static void info(Player player, String group) {
    LanguageFile lang = CCTV.get().getLang();
    CameraGroupManager cgm = CCTV.get().getCameraGroups();
    if (cgm.exists(group)) {
      CameraGroup rec = cgm.get(group);
      player.sendMessage(lang.getGroupID(group));
      String cameras = "";
      for (Camera cam : rec.getCameras())
        cameras = cameras + ", " + ChatColor.GRAY + cam.getId();
      player.sendMessage(ChatColor.GOLD + "Camera's: " + (!cameras.equals("") ? cameras.substring(2) : lang.GROUP_NO_CAMERAS_ADDED));
    } else {
      player.sendMessage(lang.GROUP_NOT_FOUND);
    } 
  }

}
