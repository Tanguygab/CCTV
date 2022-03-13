package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Command<T> {

    private final String type;

    protected final CCTV cctv = CCTV.get();
    protected final LanguageFile lang = cctv.getLang();

    public Command(String type) {
        this.type = type;
    }

    protected boolean hasPerm(Player p, String perm) {
        return p.hasPermission("cctv."+type+perm);
    }
    protected boolean canUse(Player p, String owner) {
        return owner.equals(p.getUniqueId().toString()) || hasPerm(p,".other");
    }

    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
