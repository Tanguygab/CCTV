package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.Search;
import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class computerfunctions {

  public static void setOwner(Player player, String computer, String name) {
    LanguageFile lang = CCTV.get().getLang();
    ComputerManager cpm = CCTV.get().getComputers();
    byte b;
    int i;
    OfflinePlayer[] arrayOfOfflinePlayer;
    for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
      OfflinePlayer off = arrayOfOfflinePlayer[b];
      if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(name))
        if (computer.equals("")) {
          if (player.getTargetBlock(null, 200).getType().equals(ComputerManager.COMPUTER_MATERIAL)) {
            Location loc = player.getTargetBlock(null, 200).getLocation();
            for (Computer rec : cpm.values()) {
              if (loc.equals(rec.getLocation())) {
                if (rec.getOwner().equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other")) {
                  rec.setOwner(off.getUniqueId().toString());
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
          for (Computer rec : cpm.values()) {
            if (rec.getId().equals(computer)) {
              if (rec.getOwner().equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other")) {
                rec.setOwner(off.getUniqueId().toString());
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
    List<Computer> list = CCTV.get().getComputers().values().stream().filter(c -> !(s != Search.all && (s != Search.personal || !c.getOwner().equals(p.getUniqueId().toString())) && (s != Search.name || !c.getId().toLowerCase().startsWith(search.toLowerCase())) && (s != Search.player || c.getOwner().equals("none") || !Bukkit.getOfflinePlayer(UUID.fromString(c.getOwner())).getName().startsWith(search)))).toList();
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
      if (!rec.getOwner().equals("none")) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(rec.getOwner()));
        name = off.getName();
      } 
      p.sendMessage(((s == Search.all || s == Search.player || s == Search.name) ? lang.getListAdmin(name,rec.getId()) : lang.getList(rec.getId())));
    } 
    p.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + maxpages + ChatColor.YELLOW + " =====");
  }
  
  public static void TeleportToComputer(String Computer, Player player) {
    Computer record = CCTV.get().getComputers().get(Computer);
    Location loc = record.getLocation().clone();
    loc.setY(loc.getY() + 1.0D);
    loc.setX(loc.getX() + 0.5D);
    loc.setZ(loc.getZ() + 0.5D);
    player.teleport(loc);
  }
}

