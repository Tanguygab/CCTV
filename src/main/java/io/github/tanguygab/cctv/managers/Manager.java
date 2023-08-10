package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Manager<T> {

    protected Map<String,T> map = new HashMap<>();

    protected CCTV cctv = CCTV.getInstance();
    public ConfigurationFile file;
    protected LanguageFile lang = cctv.getLang();

    public Manager() {}

    public Manager(String fileName) {
        try {
            File file = new File(cctv.getDataFolder(), fileName);
            if (!file.exists()) file.createNewFile();
            this.file = new YamlConfigurationFile(null, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void load();

    public boolean exists(String id) {
        return map.containsKey(id);
    }
    public T get(String id) {
        return map.get(id);
    }
    public List<T> values() {
        return new ArrayList<>(map.values());
    }
    @SuppressWarnings("unchecked")
    public void put(String id, Object element) {
        map.put(id, (T) element);
    }
    public void delete(String id) {
        map.remove(id);
        if (file != null) file.set(id,null);
    }
    public void delete(String id, Player player) {}
}
