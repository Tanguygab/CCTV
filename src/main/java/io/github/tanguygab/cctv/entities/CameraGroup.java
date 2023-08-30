package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CameraGroup implements Computable {

    @Setter private String name;
    @Setter private String owner;
    @Setter private Material icon;
    private final List<Computable> cameras = new ArrayList<>();
    private final BossBar bossbar;

    public CameraGroup(String name, String owner, Material icon) {
        this.name = name;
        this.owner = owner;
        this.icon = icon;
        bossbar = CCTV.getInstance().getViewers().BOSSBAR ? Bukkit.getServer().createBossBar(name, BarColor.YELLOW, BarStyle.SOLID) : null;
    }

    public void addCamera(Computable camera) {
        cameras.add(camera);
    }
    public void removeCamera(Computable camera) {
        cameras.remove(camera);
    }

    @Override
    public boolean next(Camera camera, boolean previous) {
        if (cameras.isEmpty()) return true;
        return previous ? cameras.indexOf(camera) == 0 : cameras.indexOf(camera) == cameras.size()-1;
    }

    @Override
    public boolean available(Camera camera, boolean previous) {
        if (cameras.isEmpty()) return false;
        Camera cam = get(camera,previous);
        return cam != null && cam.available(camera,previous);
    }

    @Override
    public boolean contains(Computable computable) {
        return cameras.contains(computable) || cameras.stream().anyMatch(c->c.contains(computable));
    }

    @Override
    public Camera get(Camera camera, boolean previous) {
        if (cameras.isEmpty()) return null;
        if (camera == null) cameras.get(0).get(null,previous);

        int index = cameras.indexOf(camera);
        index = index+(previous ? -1 : 1);
        if (index < 0 & previous) return cameras.get(cameras.size()-1).get(camera, true);
        if (index >= cameras.size()) return cameras.get(0).get(camera,true);

        return cameras.get(index).get(camera);
    }

    public boolean rename(String newName) {
        if (CCTV.getInstance().getGroups().rename(name,newName)) {
            name = newName;
            return true;
        }
        return false;
    }
}
