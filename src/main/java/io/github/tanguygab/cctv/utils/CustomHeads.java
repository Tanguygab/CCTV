package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomHeads {

    public final Map<String,ItemStack> heads = new LinkedHashMap<>();
    private final NamespacedKey headKey = new NamespacedKey(CCTV.get(),"head");

    public CustomHeads() {
        heads.put("_DEFAULT_",Heads.CAMERA.get());
        Map<String,String> textures = CCTV.get().getConfiguration().getConfigurationSection("camera-skins");
        textures.forEach((name,base64)-> {
            ItemStack item = Heads.createSkull(base64,name);
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(headKey, PersistentDataType.STRING,name);
            item.setItemMeta(meta);
            heads.put(name,item);
        });
    }

    public List<ItemStack> getHeads() {
        return new ArrayList<>(heads.values());
    }

    public ItemStack get(String name) {
        return heads.getOrDefault(name,Heads.CAMERA.get()).clone();
    }

    public String get(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!isCamera(item) || CCTV.get().getLang().CAMERA_ITEM_NAME.equals(meta.getDisplayName())) return "_DEFAULT_";
        return meta.getPersistentDataContainer().get(headKey, PersistentDataType.STRING);
    }

    public String findNext(String skin, boolean previous) {
        List<String> names = new ArrayList<>(heads.keySet());
        int pos = names.indexOf(skin);
        if (pos == -1) return "_DEFAULT_";
        if (previous) pos--;
        else pos++;
        if (pos < 0 || pos >= names.size()) pos = 0;
        return names.get(pos);
    }

    public boolean isCamera(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return CCTV.get().getLang().CAMERA_ITEM_NAME.equals(meta.getDisplayName()) || meta.getPersistentDataContainer().has(headKey,PersistentDataType.STRING);
    }
}
