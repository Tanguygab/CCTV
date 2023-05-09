package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.utils.Utils;
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
            List<String> cameras = config.containsKey("cameras") ? (List<String>)config.get("cameras") : new ArrayList<>();

            CameraGroup group = new CameraGroup(id,owner,cameras);
            map.put(id,group);
        });
    }

    public void create(String name, Player p) {
        LanguageFile lang = cctv.getLang();
        if (name != null && name.contains(".")) {
            p.sendMessage(lang.DOT_IN_ID);
            return;
        }
        if (exists(name)) {
            p.sendMessage(lang.GROUP_ALREADY_EXISTS);
            return;
        }
        int i = Utils.getRandomNumber(9999, "group");
        CameraGroup rec = new CameraGroup(name == null ? i+"" : name,p.getUniqueId().toString(),new ArrayList<>());
        map.put(name,rec);
        p.sendMessage(lang.GROUP_CREATE);
        p.sendMessage(lang.getGroupID(rec.getId()));
    }

    public List<String> get(Player p) {
        List<String> groups = new ArrayList<>();
        for (CameraGroup group : values()) {
            if (group.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.group.other"))
                groups.add(group.getId());
        }
        return groups;
    }
}
