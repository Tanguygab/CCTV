package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import io.github.tanguygab.cctv.menus.cameras.CameraMenu;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class Listener implements org.bukkit.event.Listener {

    private final CCTV cctv;
    private final LanguageFile lang;
    private final CameraManager cm;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public static final Map<Player,Camera> cameraRename = new HashMap<>();
    public static final Map<Player,Computer> computerAddPlayer = new HashMap<>();
    public static Map<Player,CCTVMenu> openedMenus = new HashMap<>();

    public Listener() {
        cctv = CCTV.getInstance();
        lang = cctv.getLang();
        cm = cctv.getCameras();
        cpm = cctv.getComputers();
        vm = cctv.getViewers();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || block == null) return;

        Player p = e.getPlayer();
        Computer computer = cpm.get(block);
        if (e.getHand() == EquipmentSlot.OFF_HAND || computer == null) return;
        e.setCancelled(true);
        if (computer.canUse(p)) cpm.open(p, computer);
        else p.sendMessage(CCTV.getInstance().getLang().COMPUTER_NOT_ALLOWED);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Computer computer = cpm.get(e.getBlock());
        if (computer == null) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        if (!computer.getOwner().equals(player.getUniqueId().toString()) && !player.hasPermission("cctv.computer.other")) {
            player.sendMessage(lang.COMPUTER_NOT_ALLOWED);
            return;
        }
        e.getBlock().setType(Material.AIR);

        cpm.remove(computer.getName());
        Utils.giveOrDrop(player,cpm.breakComputer(computer));
        player.sendMessage(lang.getComputerDeleted(computer.getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItemInHand();
        if (cpm.isComputer(item) != null) {
            cpm.create(item, p, e.getBlock().getLocation());
            return;
        }
        if (cm.isCamera(item)) {
            cm.createCamera(p, item, e.getBlockAgainst().getLocation(), e.getBlockAgainst().getFace(e.getBlockPlaced()));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (!(entity instanceof ArmorStand) && !(entity instanceof Creeper)) return;
        String customName = entity.getCustomName();
        if (customName == null || !ChatColor.stripColor(customName).startsWith("CAM-")) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        if (vm.exists(p)) {
            if (e.getHand() == EquipmentSlot.HAND && entity instanceof ArmorStand)
                vm.onCameraItems(p, p.getInventory().getItemInMainHand());
            e.setCancelled(true);
            return;
        }

        Camera camera = null;
        for (Camera cam : cm.values()) {
            if (cam.is(entity))
                camera = cam;
        }

        if (camera == null) {
            entity.remove();
            p.sendMessage(lang.CAMERA_DELETED_BECAUSE_BUGGED);
            return;
        }

        if (!cm.get(p).contains(camera.getName())) return;
        cctv.openMenu(p,new CameraMenu(p,camera));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if (cameraRename.containsKey(p)) {
            e.setCancelled(true);
            Camera camera = cameraRename.get(p);
            onChat(p,msg,newName->{
                if (camera.rename(newName)) {
                    cameraRename.remove(p);
                    p.sendMessage(lang.getCameraRenamed(newName));
                    Bukkit.getServer().getScheduler().runTask(cctv, () -> cctv.openMenu(p, new CameraMenu(p, camera)));
                    return;
                }
                p.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            });
        }
        if (computerAddPlayer.containsKey(p)) {
            e.setCancelled(true);
            Computer computer = computerAddPlayer.get(p);
            onChat(p,msg,newName->{
                OfflinePlayer offp = Utils.getOfflinePlayer(newName);
                if (offp == null) {
                    p.sendMessage(lang.PLAYER_NOT_FOUND);
                    return;
                }
                computerAddPlayer.remove(p);
                computer.addPlayer(offp.getUniqueId().toString());
                p.sendMessage(lang.PLAYER_ADDED);
            });
        }
    }

    private void onChat(Player p, String msg, Consumer<String> run) {
        if (msg.equals("cancel")) {
            p.sendMessage(lang.CHAT_CANCELLED);
            return;
        }

        if (msg.isEmpty()) {
            p.sendMessage(lang.COMMANDS_PROVIDE_NAME);
            return;
        }
        String newName = msg.split(" ")[0];
        run.accept(newName);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.discoverRecipe(cm.cameraKey);
        p.discoverRecipe(cpm.computerKey);
        if (!cm.EXPERIMENTAL_VIEW)
            for (Viewer viewer : vm.values()) p.hidePlayer(cctv,viewer.getPlayer());
        if (!vm.viewersQuit.containsKey(p.getUniqueId())) return;
        p.teleport(vm.viewersQuit.get(p.getUniqueId()));
        vm.viewersQuit.remove(p.getUniqueId());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (openedMenus.containsKey(p)) {
            openedMenus.get(p).onClick(e.getCurrentItem(),e.getRawSlot(),e.getClick());
            e.setCancelled(true);
            return;
        }
        if (vm.exists(p)) {
            e.setCancelled(true);
            vm.onCameraItems(p, e.getCurrentItem());
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        CCTVMenu menu = openedMenus.get(p);
        if (menu != null && menu.inv.equals(e.getInventory())) openedMenus.get(p).close();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (entity.getCustomName() != null && entity.getCustomName().startsWith("CAM-")) {
                String id = entity.getCustomName().substring(4);

                Camera cam = cm.get(id);
                if (cam == null) {
                    entity.remove();
                    return;
                }
                if (cam instanceof ArmorStand as) cam.setArmorStand(as);
                if (cam instanceof Creeper creeper) cam.setCreeper(creeper);
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        cm.loadWorld(e.getWorld());
    }

}
