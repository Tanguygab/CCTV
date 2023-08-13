package io.github.tanguygab.cctv.utils;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomHeads {

    private final Map<String,ItemStack> heads = new LinkedHashMap<>();
    private final Map<String,BarColor> barColors = new HashMap<>();
    private final CCTV cctv = CCTV.getInstance();

    public CustomHeads() {
        ItemStack defaultHead = Heads.CAMERA.get();
        ItemMeta defaultMeta = defaultHead.getItemMeta();
        assert defaultMeta != null;
        defaultMeta.getPersistentDataContainer().set(cctv.getCameras().cameraKey, PersistentDataType.STRING,"_DEFAULT_");
        defaultHead.setItemMeta(defaultMeta);
        heads.put("_DEFAULT_",defaultHead);

        Map<String,BarColor> colors = new HashMap<>() {{
            put("&c",BarColor.RED);
            put("&4",BarColor.RED);
            put("&e",BarColor.YELLOW);
            put("&6",BarColor.YELLOW);
            put("&a",BarColor.GREEN);
            put("&2",BarColor.GREEN);
            put("&b",BarColor.BLUE);
            put("&1",BarColor.BLUE);
            put("&3",BarColor.BLUE);
            put("&9",BarColor.BLUE);
            put("&d",BarColor.PINK);
            put("&5",BarColor.PURPLE);
            put("&f",BarColor.WHITE);
            put("&7",BarColor.WHITE);
            put("&8",BarColor.WHITE);
            put("&0",BarColor.PURPLE);
        }};

        Map<String,String> textures = cctv.getConfiguration().getConfigurationSection("camera.skins");
        textures.forEach((name,base64)-> {
            ItemStack item = Heads.createSkull(base64,name);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(cctv.getCameras().cameraKey, PersistentDataType.STRING,name);
            item.setItemMeta(meta);
            heads.put(name,item);
            for (String color : colors.keySet())
                if (name.contains(color))
                    barColors.put(name, colors.get(color));
        });
    }

    public List<String> getHeads() {
        return heads.keySet().stream().map(n->n.replace("_DEFAULT_","Default")).toList();
    }

    public ItemStack get(String name) {
        return heads.getOrDefault(name,Heads.CAMERA.get()).clone();
    }

    public BarColor getBarColor(String skin) {
        return barColors.getOrDefault(skin,BarColor.PURPLE);
    }
}
