package io.github.tanguygab.cctv.entities;

import org.bukkit.boss.BossBar;

public interface Computable {

    String getName();
    default boolean next(Viewer viewer, boolean previous) {
        return true;
    }
    boolean contains(Computable computable);

    default Camera get(Viewer viewer) {
        return get(viewer,true);
    }
    Camera get(Viewer viewer, boolean previous);
    BossBar getBossbar();
}
