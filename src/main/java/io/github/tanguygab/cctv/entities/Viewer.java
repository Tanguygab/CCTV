package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Viewer extends ID {

    private final ItemStack[] inv;
    private final GameMode gm;
    private Camera camera;
    private CameraGroup group;
    private Object npc;
    private final Location loc;

    public Viewer(Player p, Camera camera, CameraGroup group) {
        super(p.getUniqueId().toString(),CCTV.get().getViewers());
        inv = p.getInventory().getContents().clone();
        gm = p.getGameMode();
        this.camera = camera;
        this.group = group;
        loc = p.getLocation();
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public GameMode getGameMode() {
        return gm;
    }

    public Camera getCamera() {
        return camera;
    }
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public CameraGroup getGroup() {
        return group;
    }
    public void setGroup(CameraGroup group) {
        this.group = group;
    }

    public Object getNpc() {
        return npc;
    }
    public void setNpc(Object npc) {
        this.npc = npc;
    }

    public Location getLoc() {
        return loc;
    }
}
