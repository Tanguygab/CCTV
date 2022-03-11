package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.old.library.Search;
import io.github.tanguygab.cctv.old.records.LastClickedRecord;
import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class computerfunctions {

  
  public static void addPlayer(Player player, String name, String computer) {
    LanguageFile lang = CCTV.get().getLang();
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(name))
        if (computer.equals("")) {
          if (player.getTargetBlock(null, 200).getType().equals(ComputerUtils.getComputerMaterial())) {
            Location loc = player.getTargetBlock(null, 200).getLocation();
            for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
              if (loc.equals(rec.loc)) {
                if (!rec.allowedPlayers.contains(player.getUniqueId().toString()) &&
                  !player.hasPermission("cctv.addplayer.other")) {
                  player.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
                  return;
                } 
                if (rec.allowedPlayers.contains(off.getUniqueId().toString())) {
                  player.sendMessage(lang.PLAYER_ALREADY_ADDED);
                  return;
                } 
                rec.allowedPlayers.add(off.getUniqueId().toString());
                player.sendMessage(lang.PLAYER_ADDED);
                return;
              } 
            } 
            player.sendMessage(lang.COMPUTER_NOT_FOUND);
          } 
        } else {
          for (Computer rec : CCTV.get().getComputers().values()) {
            if (rec.getId().equals(computer)) {
              if (!rec.isAllowedPlayers(player) && !player.hasPermission("cctv.addplayer.other")) {
                player.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
                return;
              } 
              if (rec.isAllowedPlayers(off)) {
                player.sendMessage(lang.PLAYER_ALREADY_ADDED);
                return;
              } 
              rec.getAllowedPlayers().add(off.getUniqueId().toString());
              player.sendMessage(lang.PLAYER_ADDED);
              return;
            } 
          } 
          player.sendMessage(lang.COMPUTER_NOT_FOUND);
        }  
      b++;
    } 
    player.sendMessage(lang.PLAYER_NOT_FOUND);
  }
  
  public static void removePlayer(Player player, String name, String computer) {
    LanguageFile lang = CCTV.get().getLang();
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(name))
        if (computer.equals("")) {
          if (player.getTargetBlock(null, 200).getType().equals(ComputerUtils.getComputerMaterial())) {
            Location loc = player.getTargetBlock(null, 200).getLocation();
            for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
              if (loc.equals(rec.loc)) {
                if (!rec.allowedPlayers.contains(player.getUniqueId().toString()) &&
                  !player.hasPermission("cctv.removeplayer.other")) {
                  player.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
                  return;
                } 
                if (rec.allowedPlayers.contains(off.getUniqueId().toString())) {
                  for (int j = 0; j < rec.allowedPlayers.size(); j++) {
                    if (((String)rec.allowedPlayers.get(j)).equals(off.getUniqueId().toString())) {
                      rec.allowedPlayers.remove(j);
                      player.sendMessage(lang.PLAYER_REMOVED);
                    } 
                  } 
                  return;
                } 
                player.sendMessage(lang.PLAYER_NOT_IN_LIST);
                return;
              } 
            } 
            player.sendMessage(lang.COMPUTER_NOT_FOUND);
          } 
        } else {
          for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
            if (rec.id.equals(computer)) {
              if (!rec.allowedPlayers.contains(player.getUniqueId().toString()) &&
                !player.hasPermission("cctv.removeplayer.other")) {
                player.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
                return;
              } 
              if (rec.allowedPlayers.contains(off.getUniqueId().toString())) {
                for (int j = 0; j < rec.allowedPlayers.size(); j++) {
                  if (((String)rec.allowedPlayers.get(j)).equals(off.getUniqueId().toString())) {
                    rec.allowedPlayers.remove(j);
                    player.sendMessage(lang.PLAYER_REMOVED);
                  } 
                } 
                return;
              } 
              player.sendMessage(lang.PLAYER_NOT_IN_LIST);
              return;
            } 
          } 
          player.sendMessage(lang.COMPUTER_NOT_FOUND);
        }  
      b++;
    } 
    player.sendMessage(lang.PLAYER_NOT_FOUND);
  }
  
  public static boolean canPlayerAccessComputer(Player player, String computer) {
    if (computerExist(computer)) {
      ComputerRecord.computerRec rec = getComputerRecord(computer);
      if (rec.owner.equals(player.getUniqueId().toString()) || rec.allowedPlayers.contains(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other"))
        return true; 
    } 
    return false;
  }
  
  public static void setOwner(Player player, String computer, String name) {
    LanguageFile lang = CCTV.get().getLang();
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(name))
        if (computer.equals("")) {
          if (player.getTargetBlock(null, 200).getType().equals(ComputerUtils.getComputerMaterial())) {
            Location loc = player.getTargetBlock(null, 200).getLocation();
            for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
              if (loc.equals(rec.loc)) {
                if (rec.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other")) {
                  rec.owner = off.getUniqueId().toString();
                  player.sendMessage(lang.getComputerOwnerChanged(off.getName()));
                } else {
                  player.sendMessage(lang.COMPUTER_ONLY_OWNER_CAN_CHANGE_OWNER);
                } 
                return;
              } 
            } 
          } 
          player.sendMessage(lang.COMPUTER_NOT_FOUND);
        } else {
          for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
            if (rec.id.equals(computer)) {
              if (rec.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other")) {
                rec.owner = off.getUniqueId().toString();
                player.sendMessage(lang.getComputerOwnerChanged(off.getName()));
              } else {
                player.sendMessage(lang.COMPUTER_ONLY_OWNER_CAN_CHANGE_OWNER);
              } 
              return;
            } 
          } 
          player.sendMessage(lang.COMPUTER_NOT_FOUND);
        }  
      b++;
    } 
    player.sendMessage(lang.PLAYER_NOT_FOUND);
  }
  
  public static void list(Player p, int page, Search s, String search) {
    LanguageFile lang = CCTV.get().getLang();
    ArrayList<ComputerRecord.computerRec> list = new ArrayList<>();
    ComputerRecord.computers.stream().filter(c -> !(s != Search.all && (s != Search.personal || !c.owner.equals(p.getUniqueId().toString())) && (s != Search.name || !c.id.toLowerCase().startsWith(search.toLowerCase())) && (s != Search.player || c.owner.equals("none") || !Bukkit.getOfflinePlayer(UUID.fromString(c.owner)).getName().startsWith(search)))).forEach(c -> {
        
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
      p.sendMessage(lang.getListNoResult(s.toString(),search));
      return;
    } 
    if (page > maxpages || page < 1) {
      p.sendMessage(lang.TOO_MANY_PAGES);
      return;
    } 
    for (int a = (page - 1) * 8; a < 8 * page && a < list.size(); a++) {
      Computer rec = list.get(a);
      String name = "none";
      if (!rec.owner.equals("none")) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(rec.owner));
        name = off.getName();
      } 
      p.sendMessage(((s == Search.all || s == Search.player || s == Search.name) ? lang.getListAdmin(name,rec.getId()) : lang.getList(rec.getId())));
    } 
    p.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + maxpages + ChatColor.YELLOW + " =====");
  }
  
  public static ArrayList<String> getComputersFromPlayer(Player player) {
    ArrayList<String> computers = new ArrayList<>();
    for (ComputerRecord.computerRec computer : ComputerRecord.computers) {
      if (computer.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other"))
        computers.add(computer.id); 
    } 
    return computers;
  }
  
  public static void setLastClickedComputerForPlayer(Player player, Location loc) {
    for (LastClickedRecord.LastClickedRec rec : LastClickedRecord.lastclickedComputer) {
      if (rec.uuid.equals(player.getUniqueId().toString())) {
        rec.loc = loc;
        return;
      } 
    } 
    LastClickedRecord.LastClickedRec newRec = new LastClickedRecord.LastClickedRec();
    LastClickedRecord.lastclickedComputer.add(newRec);
    newRec.uuid = player.getUniqueId().toString();
    newRec.loc = loc;
  }
  
  public static Location getLastClickedComputerFromPlayer(Player player) {
    for (LastClickedRecord.LastClickedRec rec : LastClickedRecord.lastclickedComputer) {
      if (rec.uuid.equals(player.getUniqueId().toString()))
        return rec.loc; 
    } 
    return new Location(player.getWorld(), 0.0D, 0.0D, 0.0D);
  }
  
  public static Computer getComputerRecordFromLocation(Location loc) {
    for (ComputerRecord.computerRec rec : ComputerRecord.computers) {
      if (rec.loc.getBlockX() == loc.getBlockX() && rec.loc.getBlockY() == loc.getBlockY() && rec.loc.getBlockZ() == loc.getBlockZ())
        return rec; 
    } 
    return null;
  }
  
  public static void TeleportToComputer(String Computer, Player player) {
    ComputerRecord.computerRec record = getComputerRecord(Computer);
    Location locc = record.loc.clone();
    locc.setY(locc.getY() + 1.0D);
    locc.setX(locc.getX() + 0.5D);
    locc.setZ(locc.getZ() + 0.5D);
    player.teleport(locc);
  }
}

