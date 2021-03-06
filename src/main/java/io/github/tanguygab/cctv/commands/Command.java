package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Command {

    private final String type;

    protected final CCTV cctv = CCTV.get();
    protected final LanguageFile lang = cctv.getLang();

    public Command(String type) {
        this.type = type;
    }

    protected boolean noPerm(Player p, String perm) {
        return !p.hasPermission("cctv." + type + "." + perm);
    }
    protected boolean cantUse(Player p, String owner) {
        return !owner.equals(p.getUniqueId().toString()) && noPerm(p, ".other");
    }
    protected TextComponent comp(String text, ChatColor color) {
        TextComponent comp = new TextComponent(text);
        comp.setColor(color);
        comp.setBold(false);
        return comp;
    }
    protected TextComponent comp(String text, String value) {
        TextComponent comp = new TextComponent(text);
        comp.setColor(ChatColor.GOLD);
        TextComponent valueComp = new TextComponent(value);
        valueComp.setColor(ChatColor.YELLOW);
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
    protected TextComponent list(String name, List<String> list, String cmd, String hover, int page) {
        Map<Integer,List<String>> pages = new HashMap<>();
        if (list.size() < 10) pages.put(0,list);
        else list.forEach(el->{
            int pageIndex = list.indexOf(el)/10;
            if (!pages.containsKey(pageIndex))
                pages.put(pageIndex,new ArrayList<>());
            pages.get(pageIndex).add(el);
        });
        TextComponent comp = comp(name+" ("+page+"/"+pages.size()+"):\n",ChatColor.GOLD);
        comp.setBold(true);

        if (!pages.containsKey(page-1)) page = 1;

        pages.get(page-1).forEach(el->{
            TextComponent subComp = comp(" - "+el+"\n",ChatColor.YELLOW);
            subComp.setBold(false);
            subComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(new BaseComponent[]{comp(hover,ChatColor.YELLOW)})));
            subComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/cctv "+type+" "+cmd+" "+el));
            comp.addExtra(subComp);
        });

        TextComponent filler = comp("        ",ChatColor.GOLD);
        filler.setStrikethrough(true);
        comp.addExtra(filler);

        TextComponent previous = comp("\u00AB",page <= 1 ? ChatColor.DARK_GRAY : ChatColor.GRAY);
        if (page > 1) {
            previous.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW+"Previous Page")));
            previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cctv " + type + " list " + (page-1)));
        }
        comp.addExtra(previous);
        TextComponent next = comp("\u00BB",page == pages.size() ? ChatColor.DARK_GRAY : ChatColor.GRAY);
        if (page < pages.size()) {
            next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW+"Next Page")));
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cctv " + type + " list " + (page+1)));
        }
        comp.addExtra(next);
        comp.addExtra(filler);

        return comp;
    }

    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
