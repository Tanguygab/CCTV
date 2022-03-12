package io.github.tanguygab.cctv.old.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.groupfunctions;
import io.github.tanguygab.cctv.old.records.CameraGroupRecord;
import io.github.tanguygab.cctv.old.records.CameraRecord;
import io.github.tanguygab.cctv.old.records.ComputerRecord;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class cctvTabcompleter implements TabCompleter {
  private static final String[] SUBCOMMANDS = new String[] { "camera", "group", "computer", "player" };
  
  private static final String[] CAMERASUBCOMMANDS = new String[] { 
      "create", "delete", "view", "teleport", "rename", "movehere", "return", "list", "setowner", "get", 
      "debug", "connected", "disable", "enable", "search", "show", "hide" };
  
  private static final String[] GROUPSUBCOMMANDS = new String[] { "create", "delete", "addcamera", "removecamera", "setowner", "rename", "info", "list", "search" };
  
  private static final String[] COMPUTERSUBCOMMANDS = new String[] { "get", "setowner", "list", "open", "teleport", "search" };
  
  private static final String[] PLAYERSUBCOMMANDS = new String[] { "info", "restore" };
  
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    ArrayList<String> subcommands = new ArrayList<>();
    if (args.length == 1) {
      if (!args[0].equals("")) {
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = SUBCOMMANDS).length, b = 0; b < i; ) {
          String sub = arrayOfString[b];
          if (sub.toLowerCase().startsWith(args[0].toLowerCase()))
            subcommands.add(sub); 
          b++;
        } 
      } else {
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = SUBCOMMANDS).length, b = 0; b < i; ) {
          String sub = arrayOfString[b];
          subcommands.add(sub);
          b++;
        } 
      } 
      Collections.sort(subcommands);
      return subcommands;
    } 
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("camera") || args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("computer") || args[0].equalsIgnoreCase("player")) {
        String[] SUBSUBCOMMANDS = args[0].equalsIgnoreCase("camera") ? CAMERASUBCOMMANDS : (args[0].equalsIgnoreCase("group") ? GROUPSUBCOMMANDS : (args[0].equalsIgnoreCase("player") ? PLAYERSUBCOMMANDS : COMPUTERSUBCOMMANDS));
        if (!args[1].equals("")) {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = SUBSUBCOMMANDS).length, b = 0; b < i; ) {
            String sub = arrayOfString[b];
            if (sub.toLowerCase().startsWith(args[1].toLowerCase()))
              subcommands.add(sub); 
            b++;
          } 
        } else {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = SUBSUBCOMMANDS).length, b = 0; b < i; ) {
            String sub = arrayOfString[b];
            subcommands.add(sub);
            b++;
          } 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      return new ArrayList<>();
    } 
    if (args.length == 3) {
      if (args[0].equalsIgnoreCase("camera") && (args[1].equalsIgnoreCase("setowner") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("view") || args[1].equalsIgnoreCase("teleport") || args[1].equalsIgnoreCase("rename") || args[1].equalsIgnoreCase("movehere") || args[1].equalsIgnoreCase("connected") || args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("disable"))) {
        List<String> SUBSUBCOMMANDS = getListOf(args[0].toLowerCase(), sender);
        if (!args[2].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[2].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          subcommands.addAll(SUBSUBCOMMANDS);
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[0].equalsIgnoreCase("group") && (args[1].equalsIgnoreCase("addcamera") || args[1].equalsIgnoreCase("removecamera") || args[1].equalsIgnoreCase("setowner") || args[1].equalsIgnoreCase("rename") || args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("delete"))) {
        List<String> SUBSUBCOMMANDS = getListOf(args[0].toLowerCase(), sender);
        if (!args[2].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[2].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          subcommands.addAll(SUBSUBCOMMANDS);
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if ((args[0].equalsIgnoreCase("computer") && (args[1].equalsIgnoreCase("setowner") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("setgroup") || args[1].equalsIgnoreCase("open"))) || args[1].equalsIgnoreCase("teleport")) {
        List<String> SUBSUBCOMMANDS = getListOf(args[0].toLowerCase(), sender);
        if (!args[2].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[2].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          subcommands.addAll(SUBSUBCOMMANDS);
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if ((args[0].equalsIgnoreCase("player") && args[1].equalsIgnoreCase("info")) || args[1].equalsIgnoreCase("restore")) {
        List<String> SUBSUBCOMMANDS = new ArrayList<>();
        for (Player offline : Bukkit.getOnlinePlayers())
          SUBSUBCOMMANDS.add(offline.getName()); 
        if (!args[2].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[2].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[1].equalsIgnoreCase("search")) {
        String[] SUBSUBCOMMANDS = { "all", "personal", "player", "name" };
        if (!args[2].equals("")) {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = SUBSUBCOMMANDS).length, b = 0; b < i; ) {
            String sub = arrayOfString[b];
            if (sub.toLowerCase().startsWith(args[2].toLowerCase()))
              subcommands.add(sub); 
            b++;
          } 
        } else {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = SUBSUBCOMMANDS).length, b = 0; b < i; ) {
            String sub = arrayOfString[b];
            subcommands.add(sub);
            b++;
          } 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      return new ArrayList<>();
    } 
    if (args.length == 4) {
      if (args[1].equalsIgnoreCase("search") && args[2].equalsIgnoreCase("player")) {
        List<String> SUBSUBCOMMANDS = new ArrayList<>();
        byte b;
        int i;
        OfflinePlayer[] arrayOfOfflinePlayer;
        for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
          OfflinePlayer offline = arrayOfOfflinePlayer[b];
          SUBSUBCOMMANDS.add(offline.getName());
          b++;
        } 
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[1].equalsIgnoreCase("search") && args[2].equalsIgnoreCase("name")) {
        List<String> SUBSUBCOMMANDS = getListOf(args[0].toLowerCase(), sender);
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[1].equalsIgnoreCase("setowner")) {
        List<String> SUBSUBCOMMANDS = new ArrayList<>();
        byte b;
        int i;
        OfflinePlayer[] arrayOfOfflinePlayer;
        for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
          OfflinePlayer offline = arrayOfOfflinePlayer[b];
          SUBSUBCOMMANDS.add(offline.getName());
          b++;
        } 
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[0].equalsIgnoreCase("group") && (args[1].equalsIgnoreCase("addcamera") || args[1].equalsIgnoreCase("removecamera"))) {
        List<String> SUBSUBCOMMANDS = new ArrayList<>();
        if (groupfunctions.groupExist(args[2])) {
          CameraGroupRecord.groupRec groupRecord = groupfunctions.getGroupFromID(args[2]);
          for (CameraRecord.cameraRec cam : groupRecord.cameras)
            SUBSUBCOMMANDS.add(cam.id); 
        } 
        if (args[1].equalsIgnoreCase("addcamera")) {
          List<String> cameras = getListOf("camera", sender);
          for (int a = 0; a < cameras.size(); a++) {
            if (SUBSUBCOMMANDS.contains(cameras.get(a))) {
              cameras.remove(a);
              a--;
            } 
          } 
          SUBSUBCOMMANDS = cameras;
        } 
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          subcommands.addAll(SUBSUBCOMMANDS);
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[0].equalsIgnoreCase("computer") && args[1].equalsIgnoreCase("setgroup")) {
        List<String> SUBSUBCOMMANDS = getListOf("group", sender);
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      if (args[0].equalsIgnoreCase("computer") && (args[1].equalsIgnoreCase("addplayer") || args[1].equalsIgnoreCase("removeplayer"))) {
        List<String> SUBSUBCOMMANDS = new ArrayList<>();
        ComputerManager cpm = CCTV.get().getComputers();
        if (cpm.exists(args[2])) {
          Computer computerRecord = cpm.get(args[2]);
          for (String speler : computerRecord.getAllowedPlayers())
            SUBSUBCOMMANDS.add(Bukkit.getOfflinePlayer(UUID.fromString(speler)).getName()); 
        } 
        if (args[1].equalsIgnoreCase("addplayer")) {
          List<String> offlinespelers = new ArrayList<>();
          byte b;
          int i;
          OfflinePlayer[] arrayOfOfflinePlayer;
          for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
            OfflinePlayer offline = arrayOfOfflinePlayer[b];
            offlinespelers.add(offline.getName());
            b++;
          } 
          for (int a = 0; a < offlinespelers.size(); a++) {
            if (SUBSUBCOMMANDS.contains(offlinespelers.get(a))) {
              offlinespelers.remove(a);
              a--;
            } 
          } 
          SUBSUBCOMMANDS = offlinespelers;
        } 
        if (!args[3].equals("")) {
          for (String sub : SUBSUBCOMMANDS) {
            if (sub.toLowerCase().startsWith(args[3].toLowerCase()))
              subcommands.add(sub); 
          } 
        } else {
          for (String sub : SUBSUBCOMMANDS)
            subcommands.add(sub); 
        } 
        Collections.sort(subcommands);
        return subcommands;
      } 
      return new ArrayList<>();
    } 
    return new ArrayList<>();
  }
  
  public static List<String> getListOf(String args0, CommandSender sender) {
    List<String> list = new ArrayList<>();
    if (args0.equals("camera")) {
      CameraManager cm = CCTV.get().getCameras();
      if (sender instanceof Player) {
        list = cm.get((Player) sender);
      } else {
        for (Camera rec : cm.values())
          list.add(rec.getId());
      } 
    } else if (args0.equals("group")) {
      CameraGroupManager cgm = CCTV.get().getCameraGroups();
      if (sender instanceof Player) {
        list = groupfunctions.getGroupsFromPlayer((Player)sender);
      } else {
        for (CameraGroup rec : cgm.values())
          list.add(rec.getId());
      } 
    } else if (args0.equals("computer")) {
      ComputerManager cpm = CCTV.get().getComputers();
      if (sender instanceof Player p) {
        list = cpm.get(p);
      } else {
        for (Computer rec : cpm.values())
          list.add(rec.getId());
      } 
    } 
    return list;
  }
}
