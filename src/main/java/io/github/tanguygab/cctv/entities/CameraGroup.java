package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class CameraGroup implements Computable {

    @Setter private String name;
    @Setter private String owner;
    @Setter private Material icon;
    private final List<Computable> cameras = new ArrayList<>();
    private final BossBar bossbar = CCTV.getInstance().getViewers().BOSSBAR ? Bukkit.getServer().createBossBar(name, BarColor.YELLOW, BarStyle.SOLID) : null;

    public void addCamera(Computable camera) {
        cameras.add(camera);
    }
    public void removeCamera(Computable camera) {
        cameras.remove(camera);
    }

    @Override
    public boolean next(Viewer viewer) {
        return cameras.indexOf(viewer.getCamera()) == cameras.size()-1;
    }
    @Override
    public boolean contains(Computable computable) {
        return cameras.contains(computable) || cameras.stream().anyMatch(c->c.contains(computable));
    }

    @Override
    public Camera get(Viewer viewer) {
        return cameras.get(cameras.indexOf(viewer.getCamera())+1).get(viewer);
    }

    public boolean rename(String newName) {
        if (CCTV.getInstance().getGroups().rename(name,newName)) {
            name = newName;
            return true;
        }
        return false;
    }
}
