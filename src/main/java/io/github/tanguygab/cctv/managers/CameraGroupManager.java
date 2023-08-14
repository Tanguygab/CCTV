package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.entities.CameraGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CameraGroupManager extends Manager<CameraGroup> {

    public CameraGroupManager() {
        super("cameragroups.yml");
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    @Override
    protected void loadFromConfig(String key) {

    }

    @Override
    protected void saveToConfig(CameraGroup value) {

    }


    public List<String> get(Player p) {
        List<String> groups = new ArrayList<>();
        for (CameraGroup group : values())
            if (group.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.group.other"))
                groups.add(group.getName());
        return groups;
    }

}
