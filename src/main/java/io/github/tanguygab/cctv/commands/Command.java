package io.github.tanguygab.cctv.commands;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Command<T> {

    private final String type;

    protected final CCTV cctv = CCTV.getInstance();
    protected final LanguageFile lang = cctv.getLang();

    public Command(String type) {
        this.type = type;
    }

    protected abstract T get(String name);
    protected abstract String getOwner(T object);
    protected abstract void setOwner(T object, String uuid);
    protected abstract String getNotFound();
    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    protected Player getPlayer(CommandSender sender) {
        if (sender instanceof Player player) return player;
        sender.sendMessage("You have to be a player to do this!");
        return null;
    }
    protected String getFirstArg(String[] args) {
        return args.length > 1 ? args[1].toLowerCase() : "";
    }
    protected T checkExist(CommandSender player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(lang.COMMANDS_PROVIDE_NAME);
            return null;
        }
        T obj = get(args[2]);
        if (!(player instanceof Player p)) return obj;

        String owner = obj == null ? "" : getOwner(obj);

        if (obj == null || (!owner.equals(p.getUniqueId().toString()) && noPerm(player, ".other"))) {
            player.sendMessage(getNotFound());
            return null;
        }
        return obj;
    }
    protected boolean noPerm(CommandSender p, String perm) {
        return !p.hasPermission("cctv." + type + "." + perm);
    }

    protected String setOwnerCmd(CommandSender player, String[] args, String alreadyOwner) {
        T t = checkExist(player,args);
        if (t == null) return null;

        if (args.length < 4) {
            player.sendMessage(lang.COMMANDS_NEW_OWNER);
            return null;
        }
        OfflinePlayer newOwner = Utils.getOfflinePlayer(args[3]);
        if (newOwner == null) {
            player.sendMessage(lang.PLAYER_NOT_FOUND);
            return null;
        }
        String uuid = newOwner.getUniqueId().toString();
        if (getOwner(t).equals(uuid)) {
            player.sendMessage(alreadyOwner);
            return null;
        }
        setOwner(t,uuid);
        return newOwner.getName();
    }
    protected T renameCmd(CommandSender player, String[] args) {
        T t = checkExist(player,args);
        if (t == null) return null;

        if (args.length < 4) {
            player.sendMessage(lang.COMMANDS_RENAME);
            return null;
        }
        return t;
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
    protected void helpPage(CommandSender player, String info, String... commands) {
        TextComponent comp = new TextComponent();
        comp.setColor(ChatColor.GOLD);
        TextComponent strikeThrough = new TextComponent("\n                                        ");
        strikeThrough.setStrikethrough(true);
        comp.addExtra(strikeThrough);
        TextComponent infoComp = new TextComponent("\n"+info);
        infoComp.setBold(true);
        comp.addExtra(infoComp);
        for (String str : commands) {
            String[] els = str.split(":");
            comp.addExtra(comp("\n - ",ChatColor.GRAY));
            TextComponent cmdComp2 = new TextComponent("/cctv "+type+" "+els[0]);
            cmdComp2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/cctv "+type+" "+els[0]));
            comp.addExtra(cmdComp2);
            comp.addExtra(comp("\n   | ",ChatColor.DARK_GRAY));
            comp.addExtra(comp(els[1],ChatColor.YELLOW));
        }
        comp.addExtra(strikeThrough);
        player.spigot().sendMessage(comp);
    }

    protected void listCmd(CommandSender p, String name, List<String> list, String[] args) {
        int page;
        try {page = args.length < 3 ? 1 : Integer.parseInt(args[2]);}
        catch (Exception ignored) {page = 1;}

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
            subComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(lang.COMMANDS_LIST_INFO)));
            subComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/cctv "+type+" info "+el));
            comp.addExtra(subComp);
        });

        TextComponent filler = comp("        ",ChatColor.GOLD);
        filler.setStrikethrough(true);
        comp.addExtra(filler);

        TextComponent previous = comp("«",page <= 1 ? ChatColor.DARK_GRAY : ChatColor.GRAY);
        if (page > 1) {
            previous.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(lang.COMMANDS_LIST_PREVIOUS)));
            previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cctv " + type + " list " + (page-1)));
        }
        comp.addExtra(previous);
        TextComponent next = comp("»",page == pages.size() ? ChatColor.DARK_GRAY : ChatColor.GRAY);
        if (page < pages.size()) {
            next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(lang.COMMANDS_LIST_NEXT)));
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cctv " + type + " list " + (page+1)));
        }
        comp.addExtra(next);
        comp.addExtra(filler);

        p.spigot().sendMessage(comp);
    }
}
