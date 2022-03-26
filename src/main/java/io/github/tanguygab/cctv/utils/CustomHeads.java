package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomHeads {

    public final Map<String,ItemStack> heads = new LinkedHashMap<>();

    public CustomHeads() {
        heads.put("_DEFAULT_",Heads.CAMERA.get());
        Map<String,String> textures = CCTV.get().getConfiguration().getConfigurationSection("camera-skins");
        textures.forEach((name,base64)-> heads.put(name,Heads.createSkull(base64,name)));
    }

    public List<ItemStack> getHeads() {
        return new ArrayList<>(heads.values());
    }

    public ItemStack get(String name) {
        return heads.getOrDefault(name,Heads.CAMERA.get()).clone();
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
}
