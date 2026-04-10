package io.github.tanguygab.cctv.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.menus.CCTVMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.UUID;

public enum Heads {

    CAMERA("MmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=", CCTV.getInstance().getLang().CAMERA_ITEM_NAME),

    ROTATE_UP("NmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=",CCTV.getInstance().getLang().GUI_CAMERA_ROTATE_UP),
    ROTATE_LEFT("MzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=", CCTV.getInstance().getLang().GUI_CAMERA_ROTATE_LEFT), // Move Left
    ROTATE_RIGHT("NjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19", CCTV.getInstance().getLang().GUI_CAMERA_ROTATE_RIGHT), // Move Right
    ROTATE_DOWN("NzI0MzE5MTFmNDE3OGI0ZDJiNDEzYWE3ZjVjNzhhZTQ0NDdmZTkyNDY5NDNjMzFkZjMxMTYzYzBlMDQzZTBkNiJ9fX0=",CCTV.getInstance().getLang().GUI_CAMERA_ROTATE_DOWN),
    ZOOM("NTI1MGIzY2NlNzY2MzVlZjRjN2E4OGIyYzU5N2JkMjc0OTg2OGQ3OGY1YWZhNTY2MTU3YzI2MTJhZTQxMjAifX19", CCTV.getInstance().getLang().CAMERA_VIEW_OPTIONS_ZOOM_OFF), // +

    COMPUTER_BACK("Y2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19", CCTV.getInstance().getLang().GUI_COMPUTER_DEFAULT_ITEM_BACK), // Arrow Back
    MENU_PREVIOUS("YTE4NWM5N2RiYjgzNTNkZTY1MjY5OGQyNGI2NDMyN2I3OTNhM2YzMmE5OGJlNjdiNzE5ZmJlZGFiMzVlIn19fQ==", CCTV.getInstance().getLang().GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE), // Arrow Left Iron
    MENU_NEXT("MzFjMGVkZWRkNzExNWZjMWIyM2Q1MWNlOTY2MzU4YjI3MTk1ZGFmMjZlYmI2ZTQ1YTY2YzM0YzY5YzM0MDkxIn19fQ==", CCTV.getInstance().getLang().GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE), // Arrow Right Iron

    EXIT("ZTY5NTkwNThjMGMwNWE0MTdmZDc1N2NiODViNDQxNWQ5NjZmMjczM2QyZTdjYTU0ZjdiYTg2OGUzMjQ5MDllMiJ9fX0=", CCTV.getInstance().getLang().GUI_COMPUTER_DEFAULT_ITEM_EXIT),

    NIGHT_VISION_OFF("ZjNlNzFhZDkxOTUyM2VhY2U5Y2Q2MmEyNWIxOGU0ZTE3YWIzOGQxMjU2MjQxZjQyNjJkZmJhNzI5N2M0ZDkyIn19fQ==", CCTV.getInstance().getLang().CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF),
    NIGHT_VISION_ON("N2ViNGIzNDUxOWZlMTU4NDdkYmVhNzIyOTE3OWZlZWI2ZWE1NzcxMmQxNjVkY2M4ZmY2Yjc4NWJiNTg5MTFiMCJ9fX0=", CCTV.getInstance().getLang().CAMERA_VIEW_OPTIONS_NIGHTVISION_ON),
    OPTIONS("ZTYzMTVlMzgwNTQ1Yjk5ZWU5YzhkMWIyMTdjZGUyZDg4ODRhZTg3M2UwMDc0NGUwZDA1ZDc2NjNmNDE4ODJjZiJ9fX0=", "&6Options"),
    SPOTTING("ZTYzMGMyOGQ2YzhmZjIxNjZhMWFjYjc5NTMzMjJlOWY2ZDg5OWUyOGM2MWJkM2E3NzZhZTQ3ODQ5YWMyYWEifX19", "&6Spotting");

    private final ItemStack item;
    private final static String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    Heads(String texture, String name) {
        this.item = createSkull(prefix + texture, name);
    }

    public ItemStack get() {
        return this.item.clone();
    }

    public static ItemStack createSkull(String base64, String name) {
        ItemStack head = CCTVMenu.getItem(Material.PLAYER_HEAD,name);
        if (head.getItemMeta() instanceof SkullMeta meta) {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            try {
                String decoded = new String(Base64.getDecoder().decode(base64));
                String url = new Gson().fromJson(decoded, JsonObject.class)
                        .getAsJsonObject("textures")
                        .getAsJsonObject("SKIN")
                        .get("url").getAsString();
                profile.getTextures().setSkin(URI.create(url).toURL());
            } catch (MalformedURLException e) {
                CCTV.getInstance().getLogger().warning("Invalid base64 for head \"" + name + "\"");
                return head;
            }
            meta.setOwnerProfile(profile);
            head.setItemMeta(meta);
        }
        return head;
    }
}
