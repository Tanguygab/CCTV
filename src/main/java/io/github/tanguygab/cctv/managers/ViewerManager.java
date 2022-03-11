package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.utils.Heads;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class ViewerManager extends Manager<Viewer> {

    public ViewerManager() {
        super();
    }

    @Override
    public void load() {}

    @Override
    public void unload() {}

    @Override
    public void delete(String id, Player player) {
        Viewer viewer = get(id);
        if (viewer == null) {
            if (player != null) player.sendMessage("This player isn't viewing a camera!");
            return;
        }
        Player p = get(viewer);
        p.getInventory().setContents(viewer.getInv());

        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.removePotionEffect(PotionEffectType.SLOW);
        playerSetMode(p,false, viewer.getGameMode());
        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(CCTV.get(),p);
        p.teleport(viewer.getLoc());
        map.remove(id);
    }

    public void delete(Player p) {delete(p.getUniqueId().toString(),null);}
    public Viewer get(Player p) {
        return get(p.getUniqueId().toString());
    }
    public Player get(Viewer viewer) {
        return Bukkit.getServer().getPlayer(UUID.fromString(viewer.getId()));
    }

    public boolean exists(Player p) {
        return super.exists(p.getUniqueId().toString());
    }

    private static void playerSetMode(Player p, boolean mode, GameMode gm) {
        p.setCanPickupItems(!mode);
        p.setAllowFlight(mode);
        p.setGameMode(gm);
        if (gm != GameMode.CREATIVE && gm != GameMode.SURVIVAL)
            p.setFlying(mode);
        p.setCollidable(!mode);
        p.setInvulnerable(mode);
    }

    public void createPlayer(Player p, Camera cam, CameraGroup group) {
        Viewer viewer = new Viewer(p,cam,group);
        map.put(viewer.getId(),viewer);

        playerSetMode(p,true, GameMode.ADVENTURE);
        giveViewerItems(p,group);

        for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(CCTV.get(),p);
    }

    private void giveViewerItems(Player p, CameraGroup group) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (CCTV.get().CISIWP || p.hasPermission("cctv.view.zoom") || p.hasPermission("cctv.view.nightvision") || p.hasPermission("cctv.view.spot"))
            inv.setItem(0, Utils.getItem(Heads.OPTIONS,Arguments.item_camera_view_option));
        if (CCTV.get().CISIWP || p.hasPermission("cctv.view.move")) {
            inv.setItem(3, Heads.MOVE_LEFT.get());
            inv.setItem(group != null && group.getCameras().size() > 1 ? 4 : 5, Heads.MOVE_RIGHT.get());
        }
        if ((CCTV.get().CISIWP || p.hasPermission("cctv.view.switch")) && group != null && group.getCameras().size() > 1) {
            inv.setItem(6, Utils.getItem(Heads.ARROW_LEFT,Arguments.item_camera_view_group_prev));
            inv.setItem(7, Utils.getItem(Heads.ARROW_RIGHT,Arguments.item_camera_view_group_next));
        }
        inv.setItem(8, Utils.getItem(Heads.EXIT,Arguments.item_camera_view_exit));
    }


}
