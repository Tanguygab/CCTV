package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.managers.ViewerManager;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.cooldownfunctions;
import io.github.tanguygab.cctv.entities.Viewer;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class CameraUtils {

    public static Map<String,Camera> cameras = new HashMap<>();

    public static Camera getCameraFromLocation(Location loc) {
        for (Camera cam : CameraUtils.cameras.values()) {
            if (cam.getLocation().equals(loc)) return cam;
        }
        return null;
    }

    public static void renameCamera(String id, String rename, Player player) {
        LanguageFile lang = CCTV.get().getLang();
        if (!cameraExist(id)) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (CameraUtils.cameraExist(rename)) {
            player.sendMessage(lang.CAMERA_ALREADY_EXISTS);
            return;
        }
        Camera cam = cameras.get(id);
        cam.setId(rename);
        player.sendMessage(lang.getCameraRenamed(rename));
    }

    public static void viewCamera(Player player, String id, CameraGroup group) {
        LanguageFile lang = CCTV.get().getLang();
        Camera cam = CameraUtils.cameras.get(id);
        if (cam == null) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        if (cooldownfunctions.isCooldownActive(player)) return;
        if (!cam.isEnabled()) {
            if (!player.hasPermission("cctv.camera.view.override") && !player.hasPermission("cctv.admin")) {
                player.sendTitle(lang.CAMERA_OFFLINE, "",0, 15, 0);
                return;
            }
            player.sendMessage(lang.CAMERA_OFFLINE_OVERRIDE);
        }
        player.sendTitle("", lang.CAMERA_CONNECTING, 0, 15, 0);
        cooldownfunctions.addCoolDown(player, CCTV.get().TIME_TO_CONNECT);

        ViewerManager vm = CCTV.get().getViewers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCTV.get(),  () -> {
            vm.createPlayer(player, cam, group);
            NPCUtils.spawn(player, player.getLocation());
            camerafunctions.teleportToCamera(id, player);
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 60000000, 0, false, false);
            player.addPotionEffect(invisibility);
            if (group != null && vm.exists(player))
                vm.get(player).setGroup(group);
        }, CCTV.get().TIME_TO_CONNECT * 20L);
    }

    public static void unviewCamera(Player player) {
        if (player == null) return;

        ViewerManager vm = CCTV.get().getViewers();
        Viewer p = vm.get(player);
        if (p == null) return;
        NPCUtils.despawn(player,p.getNpc());
        vm.delete(player);
    }


}
