package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomHeads {

    private final Map<String,ItemStack> heads = new LinkedHashMap<>();
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

    public List<String> getHeads() {
        return heads.keySet().stream().map(n->n.replace("_DEFAULT_","Default")).toList();
    }

    public ItemStack get(String name) {
        return heads.getOrDefault(name,Heads.CAMERA.get()).clone();
    }

    public String get(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!isCamera(item) || CCTV.get().getLang().CAMERA_ITEM_NAME.equals(meta.getDisplayName())) return "_DEFAULT_";
        return meta.getPersistentDataContainer().get(headKey, PersistentDataType.STRING);
    }

    public boolean isCamera(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return CCTV.get().getLang().CAMERA_ITEM_NAME.equals(meta.getDisplayName()) || meta.getPersistentDataContainer().has(headKey,PersistentDataType.STRING);
    }
}
