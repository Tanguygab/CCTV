package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.old.functions.groupfunctions;
import io.github.tanguygab.cctv.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GroupCmd {

    public static void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("You have to be a player to do this!");
            return;
        }
        String arg = args.length > 1 ? args[1] : "";
        LanguageFile lang = CCTV.get().getLang();
        CameraGroupManager groups = CCTV.get().getCameraGroups();

        switch (arg) {
            case "create" -> {
                if (p.hasPermission("cctv.group.create")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) groups.create(args[2],p);
                else p.sendMessage(ChatColor.RED + "Please specify a group name!");
            }
            case "delete" -> {
                if (!CmdUtils.hasActionPerm(p,"group","delete")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 2) groups.delete(args[2],p);
                else p.sendMessage(ChatColor.RED + "Please specify a group name!");
            }
            case "addcamera" -> {

            }
            case "removecamera" -> {
            }
            case "setowner" -> {

            }
            case "rename" -> {
                if (!p.hasPermission("cctv.group.rename") && !p.hasPermission("cctv.group.other")) {
                    p.sendMessage(lang.NO_PERMISSIONS);
                    return;
                }
                if (args.length > 3) groupfunctions.rename(args[2], args[3], p);
                else p.sendMessage(ChatColor.RED + "Please specify a group name!");
            }
            default -> sender.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD + "Subcommands for /cctv group" + ChatColor.YELLOW
                    + "\ncreate"
                    + "\ndelete"
                    + "\naddcamera"
                    + "\nremovecamera"
                    + "\nsetowner"
                    + "\ninfo"
                    + "\nlist");
        }
    }

    public static List<String> onTabComplete(String[] args) {
        return switch (args.length) {
            case 1 -> List.of("create","delete","addcamera","removecamera","setowner","info","list");
            case 2 -> args[1].equalsIgnoreCase("list") ? List.of() : List.of("some","groups");
            case 3 -> switch (args[1]) {
                        case "addcamera" -> List.of("some","cameras");
                        case "removecamera" -> List.of("some","addedCameras");
                        case "setowner" -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
                        default -> List.of();
                    };
            default -> null;
        };
    }

}
