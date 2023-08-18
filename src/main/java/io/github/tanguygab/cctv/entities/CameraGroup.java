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
    public boolean next(Viewer viewer, boolean previous) {
        return previous ? cameras.indexOf(viewer.getCamera()) == 0 : cameras.indexOf(viewer.getCamera()) == cameras.size()-1;
    }
    @Override
    public boolean contains(Computable computable) {
        return cameras.contains(computable) || cameras.stream().anyMatch(c->c.contains(computable));
    }

    @Override
    public Camera get(Viewer viewer, boolean previous) {
        if (viewer == null) return cameras.isEmpty() ? null : cameras.get(0).get(null,previous);

        int index = cameras.indexOf(viewer.getCamera());
        if (index == -1 && previous) return cameras.get(cameras.size()-1).get(viewer,true);

        return cameras.get(index+(previous ? -1 : 1)).get(viewer);
    }

    public boolean rename(String newName) {
        if (CCTV.getInstance().getGroups().rename(name,newName)) {
            name = newName;
            return true;
        }
        return false;
    }
}
