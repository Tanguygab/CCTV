package io.github.tanguygab.cctv.entities;

import org.bukkit.boss.BossBar;

public interface Computable {

    String getName();
    default boolean next(Viewer viewer) {
        return true;
    }
    boolean contains(Computable computable);

    Camera get(Viewer viewer);
    BossBar getBossbar();
}
