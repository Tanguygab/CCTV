package io.github.tanguygab.cctv.utils;

import org.bukkit.entity.Player;

public class CmdUtils {

    public static boolean hasActionPerm(Player p, String type, String action) {
        return p.hasPermission("cctv."+type+"."+action) || p.hasPermission("cctv."+type+".other");
    }

}
