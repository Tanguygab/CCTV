package io.github.tanguygab.cctv.old.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.Search;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class cctv implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      LanguageFile lang = CCTV.get().getLang();
      if (player.hasPermission("cctv.help")) {
        if (args.length == 0) {
          player.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Subcommands for /cctv" + ChatColor.YELLOW + "\n" + "player" + "\n" + "camera" + "\n" + "group" + "\n" + "computer");
        } else if (args.length >= 1) {
          String str2, str3;
          ItemStack cam;
          int page;
          int i;
          String str1;
          switch ((str1 = args[0].toLowerCase()).hashCode()) {
            case -1367751899:
              if (!str1.equals("camera"))
                break;
              CameraManager cm = CCTV.get().getCameras();
              if (args.length == 1) {
                args = new String[] { args[0], "" };
              } else if (args.length == 2) {
                args = new String[] { args[0], args[1], "" };
              }
              switch ((str3 = args[1].toLowerCase()).hashCode()) {
                case -1360201941:
                  if (!str3.equals("teleport"))
                    break;
                  if (!player.hasPermission("cctv.camera.teleport")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args[2].length() <= 2) {
                    player.sendMessage(ChatColor.RED + "Please put in the Camera Name!");
                    return true;
                  }
                  if (cm.exists(args[2])) {
                    cm.teleport(cm.get(args[2]), player);
                  } else {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                  }
                  return true;
                case -1352294148:
                  if (!str3.equals("create"))
                    break;
                  if (!player.hasPermission("cctv.camera.create")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  cm.create(args[2], player.getLocation(), player);
                  return true;
                case -1335458389:
                  if (!str3.equals("delete"))
                    break;
                  if (!player.hasPermission("cctv.camera.delete") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args[2] != null && args[2].length() >= 1) {
                    cm.delete(args[2], player);
                  } else {
                    Camera camera = cm.get(player.getLocation());
                    cm.delete(camera == null ? null : camera.getId(), player);
                  }
                  return true;
                case -1298848381:
                  if (!str3.equals("enable"))
                    break;
                  if (!player.hasPermission("cctv.camera.enable")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length > 2 && !cm.exists(args[2])) {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                    return true;
                  }
                  camerafunctions.enable(args[2], player);
                  return true;
                case -934594754:
                  if (!str3.equals("rename"))
                    break;
                  if (!player.hasPermission("cctv.camera.rename") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length <= 3) {
                    player.sendMessage(ChatColor.RED + "Please put in the Camera Name!");
                    return true;
                  }
                  cm.renameCamera(args[2], args[3], player);
                  return true;
                case -934396624:
                  if (!str3.equals("return"))
                    break;
                  if (!player.hasPermission("cctv.camera.return")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  cm.unviewCamera(player);
                  return true;
                case -906336856:
                  if (!str3.equals("search"))
                    break;
                  if (!player.hasPermission("cctv.camera.search")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length < 4) {
                    player.sendMessage(ChatColor.YELLOW + "Use /cctv camera search <all/personal> <pagenumber>");
                    player.sendMessage(ChatColor.YELLOW + "Or /cctv camera search <player/name> <value> <pagenumber>");
                    return false;
                  }
                  if (args.length > 2 && args.length >= ((args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("personal")) ? 4 : 5))
                    if (!args[(args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("personal")) ? 3 : 4].matches("[0-9]+")) {
                      player.sendMessage(ChatColor.RED + args[(args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("personal")) ? 3 : 4] + " isn't a number!");
                      return false;
                    }
                  player.sendMessage(lang.getListSearch(args[2].toLowerCase(), (args.length >= 5) ? args[3] : ""));
                  page = (args.length < ((args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("personal")) ? 4 : 5)) ? 1 : Integer.valueOf(args[(args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("personal")) ? 3 : 4]).intValue();
                  camerafunctions.list(player, page, Search.valueOf(args[2].toLowerCase()), (args.length >= 4) ? args[3] : "");
                  return true;
                case -579210487:
                  if (!str3.equals("connected"))
                    break;
                  if (!player.hasPermission("cctv.camera.connected")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args[2] != null && args[2].length() >= 1) {
                    camerafunctions.countConnectedPlayersToCamera(args[2], player);
                  } else {
                    player.sendMessage(ChatColor.RED + "Please put in the Camera Name!");
                  }
                  return true;
                case -103826623:
                  if (!str3.equals("movehere"))
                    break;
                  if (!player.hasPermission("cctv.camera.movehere") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args[2] != null && args[2].length() >= 1) {
                    camerafunctions.moveHere(args[2], player);
                  } else {
                    player.sendMessage(ChatColor.RED + "Please put in the Camera Name!");
                  }
                  return true;
                case 102230:
                  if (!str3.equals("get"))
                    break;
                  if (!player.hasPermission("cctv.camera.get")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  cam = Heads.CAMERA.get();
                  player.getInventory().addItem(cam);
                  player.sendMessage(ChatColor.GREEN + "Place a camera with right click to make a camera!");
                  return true;
                case 3202370:
                  if (!str3.equals("hide"))
                    break;
                  if (!player.hasPermission("cctv.camera.hide")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length < 2)
                    player.sendMessage("Arguments.wrong_syntax");
                  if (args.length >= 2 && !cm.exists(args[2])) {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                    return true;
                  }
                  camerafunctions.showHideCamera(args[2], player, false);
                  return true;
                case 3322014:
                  if (!str3.equals("list"))
                    break;
                  if (!player.hasPermission("cctv.camera.list") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length > 2 && !args[2].equals("") && !args[2].matches("[0-9]+"))
                    player.sendMessage(ChatColor.RED + args[2] + " isn't a number!");
                  camerafunctions.list(player, (args.length < 3) ? 1 : (args[2].equals("") ? 1 : Integer.parseInt(args[2])), (player.hasPermission("cctv.admin") || player.hasPermission("cctv.camera.other")) ? Search.all : Search.personal, "");
                  return true;
                case 3529469:
                  if (!str3.equals("show"))
                    break;
                  if (!player.hasPermission("cctv.camera.hide")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length < 3)
                    player.sendMessage("Arguments.wrong_syntax");
                  if (args.length >= 2 && !cm.exists(args[2])) {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                    return true;
                  }
                  camerafunctions.showHideCamera(args[2], player, true);
                  return true;
                case 3619493:
                  if (!str3.equals("view"))
                    break;
                  if (!player.hasPermission("cctv.camera.view") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (!CCTV.get().getViewers().exists(player)) {
                    cm.viewCamera(player, args[2], null);
                  } else {
                    player.sendMessage(ChatColor.RED + "You already watching a camera!");
                  }
                  return true;
                case 1430430609:
                  if (!str3.equals("setowner"))
                    break;
                  if (!player.hasPermission("cctv.camera.setowner") && !player.hasPermission("cctv.camera.other")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length > 2 && !cm.exists(args[2])) {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                    return true;
                  }
                  if (args.length <= 3) {
                    player.sendMessage(ChatColor.RED + "Please put in the Camera Name and player Name!");
                    return true;
                  }
                  if (args[2] != null && args[2].length() >= 1 && args[3] != null && args[3].length() >= 1) {
                    camerafunctions.setCameraOwner(player, args[2], args[3]);
                  } else {
                    player.sendMessage(ChatColor.RED + "You are not the owner!");
                  }
                  return true;
                case 1671308008:
                  if (!str3.equals("disable"))
                    break;
                  if (!player.hasPermission("cctv.camera.disable")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (args.length > 2 && !cm.exists(args[2])) {
                    player.sendMessage(lang.CAMERA_NOT_FOUND);
                    return true;
                  }
                  camerafunctions.disable(args[2], player);
                  return true;
              }
              sender.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Subcommands for /cctv camera" + ChatColor.YELLOW + "\n" + "create" + "\n" + "delete" + "\n" + "view" + "\n" + "teleport" +
                  "\n" + "movehere" + "\n" + "return" + "\n" + "list" + "\n" + "setowner" + "\n" + "get" + "\n" + "hide" + "\n" + "show");
              return true;
              /*
            case -985752863:
              if (!str1.equals("player"))
                break;
              if (args.length == 1) {
                args = new String[] { args[0], "" };
              } else if (args.length == 2) {
                args = new String[] { args[0], args[1], "" };
              }
              switch ((str2 = args[1].toLowerCase()).hashCode()) {
                case 3237038:
                  if (!str2.equals("info"))
                    break;
                  if (!player.hasPermission("cctv.player.info")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }

                  if (inventoryfunctions.inventoryExist(args[2])) {
                    InventoryRecord.InventoryRec rec = inventoryfunctions.getLastInventoryRecord(args[2]);
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    player.sendMessage(ChatColor.DARK_GREEN + "Name: " + ChatColor.GREEN + rec.name);
                    player.sendMessage(ChatColor.DARK_GREEN + "UUID: " + ChatColor.GREEN + rec.uuid);
                    player.sendMessage(ChatColor.DARK_GREEN + "Inventory Last Saved: " + ChatColor.GREEN + dateFormat.format(rec.date));
                    player.sendMessage(ChatColor.DARK_GREEN +""+ ChatColor.STRIKETHROUGH + "---------------------------------");
                    int number = 1;
                    for (InventoryRecord.InventoryRec record : InventoryRecord.inventorylist) {
                      if (record.name.equals(args[2])) {
                        int items = 0;
                        for (int j = 0; j < record.inv.length; j++) {
                          if (record.inv[j] != null)
                            items++;
                        }
                        player.sendMessage(ChatColor.DARK_GREEN +""+ number + ". " + ChatColor.GREEN + dateFormat.format(record.date) + ChatColor.DARK_GREEN + " [itemsaved:" + ChatColor.GREEN + items + ChatColor.DARK_GREEN + "]");
                        number++;
                      }
                    }
                    break;
                  }
                  player.sendMessage(ChatColor.RED + "We don't have any saved data of this player!");
                  break;

                case 950484197:
                  if (!str2.equals("compare"))
                    break;
                  if (!player.hasPermission("cctv.player.compare")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (Bukkit.getPlayer(args[2]) != null) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (!inventoryfunctions.inventoryExist(target)) {
                      player.sendMessage(ChatColor.RED + "We don't have any saved data of this player!");
                      return true;
                    }
                    if (args.length <= 3) {
                      player.sendMessage(ChatColor.RED + "use /cctv compare <player> <inventory-id>");
                      return false;
                    }
                    if (!args[3].matches("[0-9]+") || Integer.parseInt(args[3]) == 0) {
                      player.sendMessage(ChatColor.RED + args[3] + " isn't a valid number!");
                      return false;
                    }
                    if (inventoryfunctions.getInventoryCount(target) < Integer.parseInt(args[3])) {
                      player.sendMessage(ChatColor.RED + "There isn't a inventory with the id " + args[3] + " for the player " + target.getName() + "!");
                      return false;
                    }
                    if (inventoryfunctions.compareInventory(target, Integer.parseInt(args[3]))) {
                      player.sendMessage(ChatColor.GREEN + "Players inventory is a match with the saved inventory!");
                      break;
                    }
                    player.sendMessage(ChatColor.RED + "Players inventory isn't the same as the saved inventory!");
                    break;
                  }
                  player.sendMessage(lang.PLAYER_NOT_FOUND);
                  break;
                case 1097519758:
                  if (!str2.equals("restore"))
                    break;
                  if (!player.hasPermission("cctv.player.restore")) {
                    player.sendMessage(lang.NO_PERMISSIONS);
                    return true;
                  }
                  if (Bukkit.getPlayer(args[2]) != null) {
                    Player player1 = Bukkit.getPlayer(args[2]);
                    if (!inventoryfunctions.inventoryExist(player1)) {
                      player.sendMessage(ChatColor.RED + "We don't have any saved data of this player!");
                      return true;
                    }
                    if (args.length <= 3) {
                      player.sendMessage(ChatColor.RED + "use /cctv restore <player> <inventory-id>");
                      return false;
                    }
                    if (!args[3].matches("[0-9]+") || Integer.parseInt(args[3]) == 0) {
                      player.sendMessage(ChatColor.RED + args[3] + " isn't a valid number!");
                      return false;
                    }
                    if (inventoryfunctions.getInventoryCount(player1) < Integer.parseInt(args[3])) {
                      player.sendMessage(ChatColor.RED + "There isn't a inventory with the id " + args[3] + " for the player " + player1.getName() + "!");
                      return false;
                    }
                    inventoryfunctions.restoreInventory(player1, Integer.parseInt(args[3]));
                    player.sendMessage(ChatColor.GREEN + "Players inventory is now restored to normal!");
                    break;
                  }
                  player.sendMessage(lang.PLAYER_NOT_FOUND);
                  break;
              }
              return false;
              */
          }
          player.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Subcommands for /cctv" + ChatColor.YELLOW + "\n" + "player" + "\n" + "camera" + "\n" + "group" + "\n" + "computer");
          return true;
        } 
      } else {
        player.sendMessage(ChatColor.RED + lang.NO_PERMISSIONS);
      } 
    } 
    return false;
  }
}
