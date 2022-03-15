package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Command {

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
    protected TextComponent comp(String text, ChatColor color) {
        TextComponent comp = new TextComponent(text);
        comp.setColor(color);
        comp.setBold(false);
        return comp;
    }
    protected TextComponent comp(String text, ChatColor color, String value, ChatColor valueColor) {
        TextComponent comp = new TextComponent(text);
        comp.setColor(color);
        TextComponent valueComp = new TextComponent(value);
        valueComp.setColor(valueColor);
        comp.addExtra(valueComp);
        comp.setBold(false);
        return comp;
    }
    protected TextComponent helpPage(String info, String... cmds) {
        TextComponent comp = new TextComponent();
        comp.setColor(ChatColor.GOLD);
        TextComponent strikeThrough = new TextComponent("\n                                        ");
        strikeThrough.setStrikethrough(true);
        comp.addExtra(strikeThrough);
        TextComponent infoComp = new TextComponent("\n"+info);
        infoComp.setBold(true);
        comp.addExtra(infoComp);
        for (String str : cmds) {
            String[] els = str.split(":");
            comp.addExtra(comp("\n - ",ChatColor.GRAY));
            TextComponent cmdComp2 = new TextComponent("/cctv "+type+" "+els[0]);
            cmdComp2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/cctv "+type+" "+els[0]));
            comp.addExtra(cmdComp2);
            comp.addExtra(comp("\n   | ",ChatColor.DARK_GRAY));
            comp.addExtra(comp(els[1],ChatColor.YELLOW));
        }
        comp.addExtra(strikeThrough);
        return comp;
    }

    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
