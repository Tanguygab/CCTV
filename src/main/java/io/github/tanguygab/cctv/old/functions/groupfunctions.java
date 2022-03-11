package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.library.Search;
import io.github.tanguygab.cctv.old.library.Arguments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class groupfunctions {

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

  public static void deleteGroupFromComputer(Player player) {
    if (player.getTargetBlock(null, 200).getType().equals(ComputerManager.COMPUTER_MATERIAL)) {
      Location loc = player.getTargetBlock(null, 200).getLocation();
      for (Computer rec : CCTV.get().getComputers().values()) {
        if (loc.equals(rec.getLocation())) {
          if (rec.getCameraGroup() != null) {
            rec.setCameraGroup(null);
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

  public static void setGroupOwner(Player player, String group, String target) {
    LanguageFile lang = CCTV.get().getLang();
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(target))
        if (groupExist(group)) {
          CameraGroup rec = CCTV.get().getCameraGroups().get(group);
          if (rec.getOwner().equals(player.getUniqueId().toString()) || player.hasPermission("cctv.group.other")) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(target);
            if (owner.hasPlayedBefore() || owner.isOnline()) {
              if (!rec.getOwner().equals(owner.getUniqueId().toString())) {
                rec.setOwner(owner.getUniqueId().toString());
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
    player.sendMessage(lang.PLAYER_NOT_FOUND);
  }
  
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
      player.sendMessage(Arguments.list_no_result.replaceAll("%search%", s.toString()).replaceAll("%value%", search));
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
      player.sendMessage(((s == Search.all || s == Search.player || s == Search.name) ? Arguments.list_admin : Arguments.list).replaceAll("%Player%", name).replaceAll("%ID%", rec.getId()));
    } 
    player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + maxpages + ChatColor.YELLOW + " =====");
  }
  
  public static void info(Player player, String group) {
    CameraGroupManager cgm = CCTV.get().getCameraGroups();
    if (cgm.exists(group)) {
      CameraGroup rec = cgm.get(group);
      player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", group));
      String cameras = "";
      for (Camera cam : rec.getCameras())
        cameras = cameras + ", " + ChatColor.GRAY + cam.getId();
      player.sendMessage(ChatColor.GOLD + "Camera's: " + (!cameras.equals("") ? cameras.substring(2) : Arguments.group_no_cameras_added));
    } else {
      player.sendMessage(Arguments.group_not_exist);
    } 
  }
  
  public static ArrayList<String> getGroupsFromPlayer(Player player) {

  }
  
  public static void rename(String id, String rename, Player player) {
    if (CCTV.get().getCameraGroups().exists(id)) {
      if (CCTV.get().getCameraGroups().exists(rename)) {
        player.sendMessage(Arguments.group_already_exist);
        return;
      } 
      CameraGroup group = CCTV.get().getCameraGroups().get(id);
      group.setId(rename);
      player.sendMessage(Arguments.group_renamed_to.replaceAll("%GroupID%", rename));
    } else {
      player.sendMessage(Arguments.group_not_found);
    } 
  }
}
