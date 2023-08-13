package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomHeads {

    private final Map<String,ItemStack> heads = new LinkedHashMap<>();
    private final CCTV cctv = CCTV.getInstance();

    public CustomHeads() {
        ItemStack defaultHead = Heads.CAMERA.get();
        ItemMeta defaultMeta = defaultHead.getItemMeta();
        assert defaultMeta != null;
        defaultMeta.getPersistentDataContainer().set(cctv.getCameras().cameraKey, PersistentDataType.STRING,"_DEFAULT_");
        defaultHead.setItemMeta(defaultMeta);
        heads.put("_DEFAULT_",defaultHead);

        Map<String,String> textures = cctv.getConfiguration().getConfigurationSection("camera.skins");
        textures.forEach((name,base64)-> {
            ItemStack item = Heads.createSkull(base64,name);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(cctv.getCameras().cameraKey, PersistentDataType.STRING,name);
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

}
