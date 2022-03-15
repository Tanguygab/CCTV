package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraGroupManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
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
        String itemName = item.getItemMeta().getDisplayName();

        if (vm.exists(p)) {
            e.setCancelled(true);
            if (title.matches(lang.GUI_CAMERA_SETTINGS.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"))) {
                if (e.getClickedInventory() == e.getView().getTopInventory())
                    vm.onCameraOptionsMenu(p, itemName);
                return;
            }
            vm.onCameraItems(p, itemName);
            return;
        }


        Computer computer = cpm.getLast(p);
        if (title.equals(lang.GUI_COMPUTER_OPTIONS_ITEM)) {
            e.setCancelled(true);
            onOptions(p,itemName,computer);
            return;
        }

        if (title.matches(lang.getGuiComputerDefault(null))) {
            e.setCancelled(true);
            onMain(p,itemName,computer,lang.getMatcher(lang.getGuiComputerDefault(null),title,"gui.computer.default","%page%"));
            return;
        }

        if (title.matches(lang.getGuiComputerSetGroup(null))) {
            e.setCancelled(true);
            onSetGroup(p,itemName,computer,lang.getMatcher(lang.getGuiComputerSetGroup(null),title,"gui.computer.set-group","%page%"));
            return;
        }

        if (title.matches(lang.getGuiComputerRemovePlayer(null))) {
            e.setCancelled(true);
            onRemovePlayer(p,itemName,computer,lang.getMatcher(lang.getGuiComputerRemovePlayer(null),title,"gui.computer.remove-player","%page%"));
            return;
        }

        if (title.matches(lang.getGuiCameraDelete(null))) {
            e.setCancelled(true);
            onCameraDelete(p, e.getSlot(), lang.getMatcher(lang.getGuiCameraDelete(null), title, "gui.camera.delete", "%cameraID%"));
        }
    }

    public void onMain(Player p, String item, Computer computer, String page) {
        if (computer == null) {
            p.closeInventory();
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION)) {
            if (computer.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.computer.other")) {
                openInvOptions(p);
            } else p.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
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

        double maxpages = (computer.getCameraGroup() != null ? computer.getCameraGroup().getCameras().size() : 0) / 48.0D;
        openComputer(p, checkPage(item,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE,page,maxpages), computer);
    }
    public void onOptions(Player p, String item, Computer computer) {
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_SET_CAMERA_GROUP)) {
            openSetCameraGroup(p,1);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_ADD_PLAYER)) {
            io.github.tanguygab.cctv.listeners.Listener.chatInput.add(p);
            p.closeInventory();
            p.sendMessage(lang.CHAT_PROVIDE_PLAYER);
            p.sendMessage(lang.CHAT_TYPE_EXIT);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_OPTIONS_REMOVE_PLAYER)) {
            openRemovePlayer(p,1,computer);
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            openComputer(p,1,computer);
        }
    }
    public void onSetGroup(Player p, String item, Computer computer, String page) {
        List<CameraGroup> groups = new ArrayList<>();
        for (String id : cgm.get(p)) {
            if (cgm.exists(id)) groups.add(cgm.get(id));
        }
        if (ChatColor.stripColor(item.toLowerCase()).startsWith("group:")) {
            String group = ChatColor.stripColor(item).substring(7);
            CameraGroup camGroup = cgm.get(group);
            computer.setCameraGroup(camGroup);
            openInvOptions(p);
            p.sendMessage(ChatColor.GOLD + "Group has been changed!");
            p.sendMessage(ChatColor.YELLOW + "Set to: " + camGroup.getId());
            return;
        }
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            openInvOptions(p);
            return;
        }
        double maxpages = groups.size() / 48.0D;
        openSetCameraGroup(p, checkPage(item,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE,page,maxpages));
    }
    public void onRemovePlayer(Player p, String item, Computer computer, String page) {
        if (item.equals(lang.GUI_COMPUTER_DEFAULT_ITEM_BACK)) {
            openInvOptions(p);
            return;
        }
        if (ChatColor.stripColor(item.toLowerCase()).contains("player:")) {
            String player = ChatColor.stripColor(item).substring(8);
            OfflinePlayer off = Utils.getOfflinePlayer(player);
            computer.getAllowedPlayers().remove(off.getUniqueId().toString());
            int currentpage = Integer.parseInt(page);
            openRemovePlayer(p,currentpage,computer);
            p.sendMessage(ChatColor.RED + "You removed player '" + player + "' from this computer!");
            return;
        }
        double maxpages = computer.getAllowedPlayers().size() / 48.0D;
        openRemovePlayer(p,checkPage(item,lang.GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE,lang.GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE,page,maxpages),computer);
    }
    public void onCameraDelete(Player p, int slot, String name) {
        if (slot == 3) {
            p.closeInventory();
            CCTV.get().getCameras().delete(name, p);
            return;
        }
        if (slot == 1) p.closeInventory();
    }

    private int checkPage(String item, String next, String prev, String page, double maxpages) {
        if (page == null) return -1;
        int currentpage = Integer.parseInt(page);

        if (item.equals(next) && currentpage < maxpages) return currentpage+1;
        if (item.equals(prev) && currentpage > 1) return currentpage-1;
        return -1;
    }

    private void openInvOptions(Player p) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, lang.GUI_COMPUTER_OPTIONS_ITEM);

        inv.setItem(0, Heads.COMPUTER_BACK.get());
        inv.setItem(2, Heads.COMPUTER_SET_CAMGROUP.get());
        inv.setItem(3, Heads.COMPUTER_ADD_PLAYER.get());
        inv.setItem(4, Heads.COMPUTER_REMOVE_PLAYER.get());

        p.openInventory(inv);
    }
    private void openSetCameraGroup(Player p, int page) {
        if (page == -1) return;
        Inventory inv = Bukkit.createInventory(null, 54, lang.getGuiComputerSetGroup(page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(0, holder);
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());
        List<CameraGroup> groups = new ArrayList<>();
        for (String name : cgm.get(p))
            if (cgm.exists(name)) groups.add(cgm.get(name));

        for (int a = (page - 1) * 48; a < 48 * page && a < groups.size(); a++)
            inv.addItem(Utils.getItem(Heads.CAMERA,ChatColor.GOLD + "Group: " + ChatColor.YELLOW + groups.get(a).getId()));

        p.openInventory(inv);
    }
    private void openRemovePlayer(Player p, int page, Computer computer) {
        if (page == -1) return;
        Inventory inv = Bukkit.createInventory(null, 54, lang.getGuiComputerRemovePlayer(page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(0, holder);
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Heads.COMPUTER_BACK.get());

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
    public static void openComputer(Player player, int page, Computer computer) {
        if (page == -1) return;
        LanguageFile lang = CCTV.get().getLang();
        Inventory inv = Bukkit.createInventory(null, 54, lang.getGuiComputerDefault(page+""));

        ItemStack holder = Utils.getItem(Material.BLACK_STAINED_GLASS_PANE," ");
        inv.setItem(9, holder);
        inv.setItem(18, holder);
        inv.setItem(0, Utils.getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(27, Heads.COMPUTER_NEXT.get());
        inv.setItem(36, Heads.COMPUTER_PREVIOUS.get());
        inv.setItem(45, Utils.getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));

        CameraGroup group = computer.getCameraGroup();
        if (group != null)
            for (int a = (page - 1) * 48; a < 48 * page && a < group.getCameras().size(); a++)
                inv.addItem(Utils.getItem(Heads.CAMERA,"&eCamera: " + group.getCameras().get(a).getId()));

        player.openInventory(inv);
    }
}
