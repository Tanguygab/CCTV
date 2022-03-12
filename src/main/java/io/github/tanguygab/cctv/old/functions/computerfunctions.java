package io.github.tanguygab.cctv.old.functions;

import java.util.ArrayList;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
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
  
  public static void TeleportToComputer(String Computer, Player player) {
    ComputerRecord.computerRec record = getComputerRecord(Computer);
    Location locc = record.loc.clone();
    locc.setY(locc.getY() + 1.0D);
    locc.setX(locc.getX() + 0.5D);
    locc.setZ(locc.getZ() + 0.5D);
    player.teleport(locc);
  }
}

