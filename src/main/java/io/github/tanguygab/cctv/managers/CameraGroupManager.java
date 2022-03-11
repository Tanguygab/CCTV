package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.utils.Utils;
import org.bukkit.entity.Player;

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

    public void create(String arg, Player p) {

    }

    public void removeCamera(Camera cam) {
        values().forEach(g->g.getCameras().remove(cam));
    }
}
