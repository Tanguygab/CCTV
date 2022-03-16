package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomHeads {

    private final Map<String,ItemStack> heads = new HashMap<>();

    public CustomHeads() {
        heads.put("_DEFAULT_",Heads.CAMERA.get());
        Map<String,String> textures = CCTV.get().getConfiguration().getConfigurationSection("camera-skins");
        textures.forEach((name,base64)-> heads.put(name,Heads.createSkull(base64,name)));
    }

    public List<ItemStack> getHeads() {
        return new ArrayList<>(heads.values());
    }

    public ItemStack get(String name) {
        return heads.getOrDefault(name,Heads.CAMERA.get());
    }

}
