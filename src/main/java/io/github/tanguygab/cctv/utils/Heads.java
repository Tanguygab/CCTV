package io.github.tanguygab.cctv.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.tanguygab.cctv.old.library.Arguments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public enum Heads {

    CAMERA_1("MmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=", "&9Camera"),
    CAMERA_2("YzRhNzQ0NTdjOTM1OTQ5ZGYxZmU3ZWUzOTViNDhjNDQ4YjMzZTYwMzM0MTMwOTU4NTM2YjE0OGViZDZjNiJ9fX0=", "&9Camera"),
    ARROW_LEFT("OTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ==", "&bLeft"),
    ARROW_RIGHT("MjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ==", "&bRight"),
    MOVE_LEFT("MzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=", Arguments.item_camera_view_rotate_left),
    MOVE_RIGHT("NjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19", Arguments.item_camera_view_rotate_right),
    PLUS("NTI1MGIzY2NlNzY2MzVlZjRjN2E4OGIyYzU5N2JkMjc0OTg2OGQ3OGY1YWZhNTY2MTU3YzI2MTJhZTQxMjAifX19", "&6Zoom: &4off"),
    GREEN_PLUS("NWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19", "Plus"),
    RED_MIN("NGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=", "Min"),
    CHEST("ZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19", "Chest"),
    ARROW_BACK("Y2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19", "&7Back"),
    ARROW_LEFT_IRON("YTE4NWM5N2RiYjgzNTNkZTY1MjY5OGQyNGI2NDMyN2I3OTNhM2YzMmE5OGJlNjdiNzE5ZmJlZGFiMzVlIn19fQ==", "&8Previous Page"),
    ARROW_RIGHT_IRON("MzFjMGVkZWRkNzExNWZjMWIyM2Q1MWNlOTY2MzU4YjI3MTk1ZGFmMjZlYmI2ZTQ1YTY2YzM0YzY5YzM0MDkxIn19fQ==", "&8Next Page"),
    EXIT("ZTY5NTkwNThjMGMwNWE0MTdmZDc1N2NiODViNDQxNWQ5NjZmMjczM2QyZTdjYTU0ZjdiYTg2OGUzMjQ5MDllMiJ9fX0=", "&4Exit"),
    NIGHT_VISION_OFF("ZjNlNzFhZDkxOTUyM2VhY2U5Y2Q2MmEyNWIxOGU0ZTE3YWIzOGQxMjU2MjQxZjQyNjJkZmJhNzI5N2M0ZDkyIn19fQ==", "&6Night Vision: &4off"),
    NIGHT_VISION_ON("N2ViNGIzNDUxOWZlMTU4NDdkYmVhNzIyOTE3OWZlZWI2ZWE1NzcxMmQxNjVkY2M4ZmY2Yjc4NWJiNTg5MTFiMCJ9fX0=", "&6Night Vision: &aon"),
    OPTIONS("ZTYzMTVlMzgwNTQ1Yjk5ZWU5YzhkMWIyMTdjZGUyZDg4ODRhZTg3M2UwMDc0NGUwZDA1ZDc2NjNmNDE4ODJjZiJ9fX0=", "&6Options"),
    SPOTTING("ZTYzMGMyOGQ2YzhmZjIxNjZhMWFjYjc5NTMzMjJlOWY2ZDg5OWUyOGM2MWJkM2E3NzZhZTQ3ODQ5YWMyYWEifX19", "&6Spotting");

    private final ItemStack item;

    Heads(String texture, String skullname) {
        String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
        this.item = createSkull(prefix + texture, skullname);
    }

    public ItemStack get() {
        return this.item.clone();
    }

    private static ItemStack createSkull(String base64, String SkullName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SkullName));
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
