package io.github.tanguygab.cctv.managers;

import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CameraGroupManager extends Manager<CameraGroup> {

    private final List<Material> allowedIcons = new ArrayList<>();

    public CameraGroupManager() {
        super("cameragroups.yml");
    }

    @Override
    public void load() {
        cctv.getConfiguration().getStringList("camera.group-icons",List.of("CHEST","ENDER_CHEST","SHULKER_BOX","BARREL")).forEach(icon->{
            Material material = Material.getMaterial(icon);
            if (material != null) allowedIcons.add(material);
        });

        file.getValues().keySet().forEach(this::loadFromConfig);
        // loading cameras after all groups are loaded because they can also contain groups
        values().forEach(group->file.getStringList("cameras").forEach(camera->{
            Computable c = camera.startsWith("group.") ? get(camera.substring(6)) : cctv.getCameras().get(camera);
            if (c != null) group.addCamera(c);
        }));
    }

    @Override
    public void unload() {
        values().forEach(this::saveToConfig);
    }

    @Override
    protected void loadFromConfig(String name) {
        String owner = file.getString(name+".owner");
        String icon = file.getString(name+".icon","CHEST");
        Material material = Material.getMaterial(icon);
        create(name,owner,allowedIcons.contains(material) ? material : allowedIcons.get(0));
    }

    public List<String> getAllowedIcons() {
        return allowedIcons.stream().map(Material::toString).toList();
    }

    @Override
    protected void saveToConfig(CameraGroup group) {
        String name = group.getName();
        set(name,"owner",group.getOwner());
        set(name,"icon",group.getIcon().toString());
        set(name,"cameras",group.getCameras().stream().map(c->(c instanceof CameraGroup ? "group.":"")+c.getName()).toList());
    }

    private void create(String name, String owner, Material icon) {
        put(name,new CameraGroup(name,owner,icon));
    }
    public void create(String name, Player player) {
        if (name == null) name = getRandomID();
        if (name.contains(".")) {
            player.sendMessage(lang.DOT_IN_NAME);
            return;
        }
        if (exists(name)) {
            player.sendMessage(lang.GROUP_ALREADY_EXISTS);
            return;
        }
        create(name,player.getUniqueId().toString(),allowedIcons.get(0));
        player.sendMessage(lang.getGroupCreated(name));
    }

    public List<String> get(Player p) {
        List<String> groups = new ArrayList<>();
        for (CameraGroup group : values())
            if (group.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.group.other"))
                groups.add(group.getName());
        return groups;
    }
}
