package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvClickEvent implements Listener {

    private final LanguageFile lang;
    private final CameraGroupManager cgm;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public InvClickEvent() {
        lang = CCTV.get().getLang();
        cgm = CCTV.get().getCameraGroups();
        cpm = CCTV.get().getComputers();
        vm = CCTV.get().getViewers();
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        String title = e.getView().getTitle();

        if (vm.exists(p)) {
            e.setCancelled(true);
            if (title.matches(lang.GUI_CAMERA_SETTINGS.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"))) {
                if (e.getClickedInventory() == e.getView().getTopInventory())
                    viewfunctions.settingFunctions(p, item);
                return;
            }
            viewfunctions.switchFunctions(p, item);
            return;
        }

        Computer computer = cpm.getLast(p);
        String itemName = item.getItemMeta().getDisplayName();
        e.setCancelled(true);
        if (title.matches(quote(lang.GUI_COMPUTER_DEFAULT).replaceAll("%page%", "*\\\\d+"))) onMain(p,title,itemName,computer);
        if (title.matches(quote(lang.GUI_COMPUTER_OPTIONS_ITEM))) onOptions(p,itemName,computer);
        if (title.matches(quote(lang.GUI_COMPUTER_SET_GROUP).replaceAll("%page%", "*\\\\d+"))) onSetGroup(p,title,itemName,computer);
        if (title.matches(quote(lang.GUI_COMPUTER_REMOVE_PLAYER).replaceAll("%page%", "*\\\\d+"))) onRemovePlayer(p,title,itemName,computer);
        if (title.matches(quote(lang.GUI_CAMERA_DELETE).replaceAll("%CameraID%", "*.+"))) {
            onCameraDelete(p,title, e.getSlot());
        }
    }

    public void onMain(Player p, String title, String item, Computer computer) {
        if (computer == null) {
            p.closeInventory();
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION)) {
            if (Utils.canUse(computer.getOwner(),p,"computer.other")) {
                optionsInv(p);
            } else p.sendMessage(lang.NO_PERMISSIONS);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT)) {
            p.closeInventory();
            return;
        }
        if (ChatColor.stripColor(item).startsWith("Camera:")) {
            String camera = ChatColor.stripColor(item).substring(8);
            CCTV.get().getCameras().viewCamera(p, camera, computer.getCameraGroup());
            p.closeInventory();
            return;
        }

        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_DEFAULT.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_default");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            double maxpages = (computer.getCameraGroup() != null ? computer.getCameraGroup().getCameras().size() : 0) / 48.0D;
            if (currentpage >= maxpages) return;
            openComputer(p, currentpage+1, computer);
            return;
        }

        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_DEFAULT.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_default");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            if (currentpage == 1) return;
            openComputer(p, currentpage - 1, computer);
        }
    }
    public void onOptions(Player p, String item, Computer computer) {
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_SET_CAMERA_GROUP)) {
            setCameraGroup(p,1);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_ADD_PLAYER)) {
            CCTV.get().chatInput.add(p);
            p.closeInventory();
            p.sendMessage(Arguments.chat_set_name_to_add);
            p.sendMessage(Arguments.chat_type_exit);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_REMOVE_PLAYER)) {
            removePlayer(p,1,computer);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            camerafunctions.getCCTVFromComputer(p, computer.getLocation());
        }
    }
    public void onSetGroup(Player p, String title, String item, Computer computer) {
        List<CameraGroup> groups = new ArrayList<>();
        for (String id : cgm.get(p)) {
            if (cgm.exists(id)) groups.add(cgm.get(id));
        }
        if (ChatColor.stripColor(item.toLowerCase()).startsWith("group:")) {
            String group = ChatColor.stripColor(item).substring(7);
            CameraGroup camGroup = cgm.get(group);
            computer.setCameraGroup(camGroup);
            optionsInv(p);
            p.sendMessage(ChatColor.GOLD + "Group has been changed!");
            p.sendMessage(ChatColor.YELLOW + "Set to: " + camGroup.getId());
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            optionsInv(p);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_SET_GROUP.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_setgroup");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            double maxpages = groups.size() / 48.0D;
            if (currentpage >= maxpages) return;
            setCameraGroup(p, currentpage + 1);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_SET_GROUP.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_setgroup");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            if (currentpage == 1) return;
            setCameraGroup(p, currentpage - 1);
        }
    }
    public void onRemovePlayer(Player p, String title, String item, Computer computer) {
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            optionsInv(p);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_SET_GROUP.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_removeplayer");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            double maxpages = computer.getAllowedPlayers().size() / 48.0D;
            if (currentpage >= maxpages) return;
            removePlayer(p,currentpage + 1,computer);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE)) {
            String pageMatch = getMatcher(lang.GUI_COMPUTER_SET_GROUP.replaceAll("%page%", "*\\(\\\\d+\\)"),title,"gui_computer_removeplayer");
            if (pageMatch == null) return;
            int currentpage = Integer.parseInt(pageMatch);
            if (currentpage == 1) return;
            removePlayer(p,currentpage - 1,computer);
            return;
        }
        if (ChatColor.stripColor(item.toLowerCase()).contains("player:")) {
            String play = ChatColor.stripColor(item).substring(8);
            OfflinePlayer off = Utils.getOfflinePlayer(play);
            for (String uuid : computer.getAllowedPlayers()) {
                if (!off.getUniqueId().toString().equals(uuid)) continue;
                computer.getAllowedPlayers().remove(uuid);
                int currentpage = Integer.parseInt(ChatColor.stripColor(title.substring(23, title.length() - 1)));
                removePlayer(p,currentpage,computer);
                p.sendMessage(ChatColor.RED + "You removed player '" + play + "' from this computer!");
                return;
            }
        }
    }
    public void onCameraDelete(Player p, String title, int slot) {
        if (slot == 5) {
            p.closeInventory();
            String name = getMatcher(lang.GUI_CAMERA_DELETE.replaceAll("%CameraID%", "*\\(.+\\)"),title,"gui_computer_removeplayer");
            CCTV.get().getCameras().delete(name, p);
            return;
        }
        if (slot == 3) p.closeInventory();
    }

    private String quote(String str) {
        return str.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
    }
    private String getMatcher(String patt, String match, String string) {
        Pattern pattern = Pattern.compile(patt.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"));
        Matcher m = pattern.matcher(match);
        if (!m.matches()) {
            CCTV.get().getLogger().info("The language message '"+string+"' doesn't contain %page%!");
            return null;
        }
        return m.group(1);
    }

    private void optionsInv(Player p) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, lang.GUI_COMPUTER_OPTIONS_ITEM);

        inv.setItem(0, Utils.getItem(Heads.CHEST,lang.GUI_COMPUTER_OPTIONS_SET_CAMERA_GROUP));
        inv.setItem(1, Utils.getItem(Heads.GREEN_PLUS,lang.GUI_COMPUTER_OPTIONS_ADD_PLAYER));
        inv.setItem(2, Utils.getItem(Heads.RED_MIN,lang.GUI_COMPUTER_OPTIONS_REMOVE_PLAYER));
        inv.setItem(4, Utils.getItem(Heads.ARROW_BACK,lang.GUI_COMPUTER_DEFAULT_ITEM_BACK));

        p.openInventory(inv);
    }
    public void setCameraGroup(Player p, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, lang.GUI_COMPUTER_SET_GROUP.replaceAll("%page%", page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(0, holder);
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(27, Utils.getItem(Heads.ARROW_RIGHT_IRON,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE));
        inv.setItem(36, Utils.getItem(Heads.ARROW_LEFT_IRON,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE));
        inv.setItem(45, Utils.getItem(Heads.ARROW_BACK,lang.GUI_COMPUTER_DEFAULT_ITEM_BACK));
        List<CameraGroup> groups = new ArrayList<>();
        for (String name : cgm.get(p))
            if (cgm.exists(name)) groups.add(cgm.get(name));

        for (int a = (page - 1) * 48; a < 48 * page && a < groups.size(); a++)
            inv.addItem(Utils.getItem(Heads.CAMERA_1,ChatColor.GOLD + "Group: " + ChatColor.YELLOW + groups.get(a).getId()));

        p.openInventory(inv);
    }
    public void removePlayer(Player p, int page, Computer computer) {
        Inventory inv = Bukkit.createInventory(null, 54, lang.GUI_COMPUTER_REMOVE_PLAYER.replaceAll("%page%", page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(0, holder);
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(27, Utils.getItem(Heads.ARROW_RIGHT_IRON,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE));
        inv.setItem(36, Utils.getItem(Heads.ARROW_LEFT_IRON,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE));
        inv.setItem(45, Utils.getItem(Heads.ARROW_BACK,lang.GUI_COMPUTER_DEFAULT_ITEM_BACK));

        for (int a = (page - 1) * 48; a < 48 * page && a < computer.getAllowedPlayers().size(); a++) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(computer.getAllowedPlayers().get(a)));
            ItemStack item = Utils.getItem(Material.PLAYER_HEAD,ChatColor.YELLOW + "player: " + off.getName());
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwningPlayer(off);
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        p.openInventory(inv);
    }
    public void openComputer(Player player, int page, Computer computer) {
        Inventory inv = Bukkit.createInventory(null, 54, lang.GUI_COMPUTER_DEFAULT.replaceAll("%page%", page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(0, Utils.getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(27, Utils.getItem(Heads.ARROW_RIGHT_IRON,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE));
        inv.setItem(36, Utils.getItem(Heads.ARROW_LEFT,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE));
        inv.setItem(45, Utils.getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));

        CameraGroup group = computer.getCameraGroup();
        if (group == null)
            for (int a = (page - 1) * 48; a < 48 * page && a < group.getCameras().size(); a++)
                inv.addItem(Utils.getItem(Heads.CAMERA_1,"&eCamera: " + group.getCameras().get(a).getId()));

        player.openInventory(inv);
    }
}
