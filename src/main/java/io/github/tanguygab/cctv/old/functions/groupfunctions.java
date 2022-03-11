package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.old.library.Search;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.utils.ComputerUtils;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class groupfunctions {
  public static void addCameraToGroup(Player player, String group, String camera) {
    CameraManager cm = CCTV.get().getCameras();
    if (cm.exists(camera) && groupExist(group)) {
      Camera rec = cm.get(camera);
      CameraGroup grec = CCTV.get().getCameraGroups().get(group);
      if (grec.getCameras().contains(rec)) {
        player.sendMessage(Arguments.group_camera_already_added);
        return;
      } 
      grec.getCameras().add(rec);
      player.sendMessage(Arguments.group_camera_added);
      player.sendMessage(Arguments.camera_id.replaceAll("%CameraID%", rec.getId()));
      player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", grec.getId()));
      return;
    } 
    player.sendMessage(Arguments.group_or_camera_not_exist);
  }
  
  public static void deleteCameraFromGroup(Player player, String group, String camera) {
    if (camera != null && group != null) {
      if (groupExist(group)) {
        CameraGroupRecord.groupRec rec = getGroupFromID(group);
        for (int a = 0; a < rec.cameras.size(); a++) {
          if (((CameraRecord.cameraRec)rec.cameras.get(a)).id.equals(camera)) {
            rec.cameras.remove(a);
            player.sendMessage(Arguments.group_delete_camera);
            return;
          } 
        } 
        player.sendMessage(Arguments.group_contains_not_camera);
        return;
      } 
      player.sendMessage(Arguments.group_not_exist);
    } else {
      player.sendMessage(Arguments.wrong_syntax);
    } 
  }
  
  public static void addGroupToComputer(Player player, String group) {
    if (group != null) {
      if (player.getTargetBlock(null, 200).getType().equals(ComputerUtils.getComputerMaterial())) {
        Location loc = player.getTargetBlock(null, 200).getLocation();
        for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
          if (loc.equals(rec.loc)) {
            CameraGroupRecord.groupRec grec = getGroupFromID(group);
            if (groupExist(group)) {
              rec.cameraGroup = grec;
              player.sendMessage(Arguments.group_set_to_computer);
              player.sendMessage(Arguments.computer_id.replaceAll("%ComputerID%", rec.id));
              player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", grec.id));
              return;
            } 
            player.sendMessage(Arguments.group_not_exist);
            return;
          } 
        } 
        player.sendMessage(Arguments.computer_not_exist);
      } 
    } else {
      player.sendMessage(Arguments.wrong_syntax);
    } 
  }
  
  public static boolean groupExist(String id) {
    for (CameraGroupRecord.groupRec rec : CameraGroupRecord.CameraGroups) {
      if (rec.id.equals(id))
        return true; 
    } 
    return false;
  }
  
  public static void deleteGroupFromComputer(Player player) {
    if (player.getTargetBlock(null, 200).getType().equals(ComputerUtils.getComputerMaterial())) {
      Location loc = player.getTargetBlock(null, 200).getLocation();
      for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
        if (loc.equals(rec.loc)) {
          if (rec.cameraGroup != null) {
            rec.cameraGroup = null;
            player.sendMessage(Arguments.group_removed_from_computer);
          } else {
            player.sendMessage(Arguments.computer_no_group_set);
          } 
          return;
        } 
      } 
      player.sendMessage(Arguments.computer_not_exist);
    } 
  }
  
  public static void CreateGroup(Player player, String name) {
    if (!groupExist(name)) {
      int i = Utils.getRandomNumber(9999, "group");
      CameraGroupRecord.groupRec rec = new CameraGroupRecord.groupRec();
      CameraGroupRecord.CameraGroups.add(rec);
      rec.id = name.equals("") ? i+"" : name;
      rec.owner = player.getUniqueId().toString();
      player.sendMessage(Arguments.group_create);
      player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", rec.id));
    } else {
      player.sendMessage(Arguments.group_already_exist);
    } 
  }
  
  public static void DeleteGroup(Player player, String name) {

  }
  
  public static CameraGroupRecord.groupRec getGroupFromID(String group) {
    for (CameraGroupRecord.groupRec rec : CameraGroupRecord.CameraGroups) {
      if (rec.id.equalsIgnoreCase(group))
        return rec; 
    } 
    return null;
  }
  
  public static void setGroupOwner(Player player, String group, String target) {
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(target))
        if (groupExist(group)) {
          CameraGroupRecord.groupRec rec = getGroupFromID(group);
          if (rec.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.group.other")) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(target);
            if (owner != null) {
              if (!rec.owner.equals(owner.getUniqueId().toString())) {
                rec.owner = owner.getUniqueId().toString();
                player.sendMessage(Arguments.group_owner_set_to.replaceAll("%Player%", owner.getName()));
                return;
              } 
              player.sendMessage(Arguments.group_player_already_owner);
            } else {
              player.sendMessage(Arguments.player_not_found);
            } 
          } else {
            player.sendMessage(Arguments.group_only_change_your_own);
          } 
        } else {
          player.sendMessage(Arguments.group_not_exist);
        }  
      b++;
    } 
    player.sendMessage(Arguments.player_not_found);
  }
  
  public static void list(Player player, int page, Search s, String search) {
    LanguageFile lang = CCTV.get().getLang();
    ArrayList<CameraGroupRecord.groupRec> list = new ArrayList<>();
    CameraGroupRecord.CameraGroups.stream().filter(c -> !(s != Search.all && (s != Search.personal || !c.owner.equals(player.getUniqueId().toString())) && (s != Search.name || !c.id.toLowerCase().startsWith(search.toLowerCase())) && (s != Search.player || c.owner.equals("none") || !Bukkit.getOfflinePlayer(UUID.fromString(c.owner)).getName().startsWith(search)))).forEach(g -> {
        
        });
    list.sort((c1, c2) -> {
      String name1 = "none";
      String name2 = "none";
      if (!c1.owner.equals("none")) {
        OfflinePlayer off1 = Bukkit.getOfflinePlayer(UUID.fromString(c1.owner));
        name1 = off1.getName();
      }
      if (!c2.owner.equals("none")) {
        OfflinePlayer off2 = Bukkit.getOfflinePlayer(UUID.fromString(c2.owner));
        name2 = off2.getName();
      }
      return name2.compareTo(name1);
    });
    int maxpages = (int)Math.ceil(list.size() / 8.0D);
    if (page > maxpages && page == 1) {
      player.sendMessage(Arguments.list_no_result.replaceAll("%search%", s.toString()).replaceAll("%value%", search));
      return;
    } 
    if (page > maxpages || page < 1) {
      player.sendMessage(lang.TOO_MANY_PAGES);
      return;
    } 
    for (int a = (page - 1) * 8; a < 8 * page && a < list.size(); a++) {
      CameraGroupRecord.groupRec rec = list.get(a);
      String name = "none";
      if (!rec.owner.equals("none")) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(rec.owner));
        name = off.getName();
      } 
      player.sendMessage(((s == Search.all || s == Search.player || s == Search.name) ? Arguments.list_admin : Arguments.list).replaceAll("%Player%", name).replaceAll("%ID%", rec.id));
    } 
    player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + maxpages + ChatColor.YELLOW + " =====");
  }
  
  public static void info(Player player, String group) {
    if (groupExist(group)) {
      CameraGroupRecord.groupRec rec = getGroupFromID(group);
      player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", group));
      String cameras = "";
      for (CameraRecord.cameraRec cam : rec.cameras)
        cameras = cameras + ", " + ChatColor.GRAY + cam.id;
      player.sendMessage(ChatColor.GOLD + "Camera's: " + (!cameras.equals("") ? cameras.substring(2) : Arguments.group_no_cameras_added));
    } else {
      player.sendMessage(Arguments.group_not_exist);
    } 
  }
  
  public static ArrayList<String> getGroupsFromPlayer(Player player) {
    ArrayList<String> groups = new ArrayList<>();
    for (CameraGroupRecord.groupRec group : CameraGroupRecord.CameraGroups) {
      if (group.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.group.other"))
        groups.add(group.id); 
    } 
    return groups;
  }
  
  public static void rename(String id, String rename, Player player) {
    if (groupExist(id)) {
      if (groupExist(rename)) {
        player.sendMessage(Arguments.group_already_exist);
        return;
      } 
      CameraGroupRecord.groupRec group = getGroupFromID(id);
      group.id = rename;
      player.sendMessage(Arguments.group_renamed_to.replaceAll("%GroupID%", rename));
    } else {
      player.sendMessage(Arguments.group_not_found);
    } 
  }
}
