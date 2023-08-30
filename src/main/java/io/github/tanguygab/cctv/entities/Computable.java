package io.github.tanguygab.cctv.entities;

import org.bukkit.boss.BossBar;

public interface Computable {

    String getName();
    default boolean next(Camera camera, boolean previous) {
        return true;
    }
    boolean available(Camera camera, boolean previous);
    boolean contains(Computable computable);

    default Camera get(Camera camera) {
        return get(camera,true);
    }
    Camera get(Camera camera, boolean previous);
    BossBar getBossbar();
}
