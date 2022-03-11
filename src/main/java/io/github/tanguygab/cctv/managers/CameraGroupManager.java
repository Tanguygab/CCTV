package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraGroupManager extends Manager<CameraGroup> {

    public CameraGroupManager() {
        super("cameragroups.yml");
    }

    @Override
    public void load() {
        Map<String, Object> cams = file.getValues();
        cams.forEach((id,cfg)-> {
            Map<String, Object> config = (Map<String, Object>) cfg;
            String owner = config.get("owner")+"";
            List<String> cameras = (List<String>)config.get("cameras");

            CameraGroup group = new CameraGroup(id,owner,cameras);
            map.put(id,group);
        });
    }

    @Override
    public void unload() {
        map.forEach((id, group)->{
            file.set(id + ".owner", group.getOwner());
            file.set(id + ".cameras", group.getCameras());
        });
    }

    @Override
    public void delete(String id, Player player) {
        CameraGroup group = get(id);
        if (group == null || Utils.canUse(group.getOwner(),player,"group.other")) {
            player.sendMessage(Arguments.group_not_exist);
            return;
        }
        player.sendMessage(Arguments.group_delete);
    }

    public void create(String name, Player p) {
        if (exists(name)) {
            p.sendMessage(Arguments.group_already_exist);
            return;
        }
        int i = Utils.getRandomNumber(9999, "group");
        CameraGroup rec = new CameraGroup(name == null ? i+"" : name,p.getUniqueId().toString(),new ArrayList<>());
        map.put(name,rec);
        p.sendMessage(Arguments.group_create);
        p.sendMessage(Arguments.group_id.replaceAll("%GroupID%", rec.getId()));
    }

    public void removeCamera(Camera cam) {
        values().forEach(g->g.getCameras().remove(cam));
    }

    public List<String> get(Player p) {
        List<String> groups = new ArrayList<>();
        for (CameraGroup group : values()) {
            if (group.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.group.other"))
                groups.add(group.getId());
        }
        return groups;
    }

    public void addCamera(Player player, String group, String camera) {
        CameraManager cm = CCTV.get().getCameras();
        if (!cm.exists(camera) || !exists(group)) {
            player.sendMessage(Arguments.group_or_camera_not_exist);
            return;
        }
        CameraGroup camGroup = get(group);
        Camera cam = cm.get(camera);
        if (camGroup.getCameras().contains(cam)) {
            camGroup.getCameras().add(cam);
            player.sendMessage(Arguments.group_camera_added);
            player.sendMessage(lang.getCameraID(cam.getId()));
            player.sendMessage(Arguments.group_id.replaceAll("%GroupID%", camGroup.getId()));
        } else player.sendMessage(Arguments.group_camera_already_added);
    }
    public void removeCamera(Player player, String group, String camera) {
        CameraManager cm = CCTV.get().getCameras();
        if (!exists(group) || !cm.exists(camera)) {
            player.sendMessage(Arguments.group_camera_already_added);
            return;
        }
        CameraGroup camGroup = get(group);
        Camera cam = cm.get(camera);
        if (camGroup.getCameras().contains(cam)) {
            camGroup.getCameras().remove(cam);
            player.sendMessage(Arguments.group_delete_camera);
        } else player.sendMessage(Arguments.group_contains_not_camera);
    }
}
