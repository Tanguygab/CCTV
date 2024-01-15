package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.ConfigurationFile;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.config.YamlConfigurationFile;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public abstract class Manager<T> {

    protected Map<String,T> map = new HashMap<>();

    protected CCTV cctv = CCTV.getInstance();
    public ConfigurationFile file;
    protected LanguageFile lang = cctv.getLang();
    private static final Random random = new Random();

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
    public abstract void unload();

    public boolean exists(String id) {
        return map.containsKey(id);
    }
    public T get(String key) {
        return map.get(key);
    }
    public List<T> values() {
        return new ArrayList<>(map.values());
    }
    @SuppressWarnings("unchecked")
    public void put(String key, Object element) {
        map.put(key, (T) element);
    }
    public boolean rename(String name, String newName) {
        if (exists(newName)) return false;
        map.put(newName,get(name));
        remove(name);
        return true;
    }
    public void remove(String key) {
        map.remove(key);
        if (!(this instanceof ViewerManager)) file.set(key,null);
    }

    public String getRandomID() {
        int number = random.nextInt(999999);
        return map.containsKey(String.valueOf(number)) ? getRandomID() : String.valueOf(number);
    }

    protected void set(String id, String path, Object value) {
        file.set(id+"."+path,value);
    }
    protected abstract void loadFromConfig(String key);
    protected abstract void saveToConfig(T value);
}
