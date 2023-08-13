package io.github.tanguygab.cctv;


import io.github.tanguygab.cctv.entities.Camera;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class CCTVExpansion extends PlaceholderExpansion {

    private final CCTV cctv;
    private final List<String> placeholders = new ArrayList<>();

    public CCTVExpansion(CCTV cctv) {
        this.cctv = cctv;
        List.of("viewers","viewercount","is_viewed").forEach(p->placeholders.add("%cctv_camera_<camera>_"+p+"%"));
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cctv";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public @NotNull String getVersion() {
        return cctv.getDescription().getVersion();
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return placeholders;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!params.startsWith("camera_")) return null;
        String cam = params.split("_")[1];
        Camera camera = cctv.getCameras().get(cam);
        if (camera == null) return null;
        params = params.substring(8+cam.length());
        return switch (params) {
            case "viewers" -> cctv.getViewers().values().stream()
                    .filter(v -> v.getCamera() == camera)
                    .map(v -> cctv.getServer().getPlayer(UUID.fromString(v.getId())))
                    .filter(Objects::nonNull)
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
            case "viewercount" -> String.valueOf(cctv.getViewers().values().stream().filter(v -> v.getCamera() == camera).count());
            case "is_viewed" -> String.valueOf(cctv.getViewers().values().stream().anyMatch(v -> v.getCamera() == camera));
            default -> null;
        };
    }
}
