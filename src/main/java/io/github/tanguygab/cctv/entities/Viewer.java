package io.github.tanguygab.cctv.entities;

import net.minecraft.server.level.EntityPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Viewer extends ID {

    private final ItemStack[] inv;
    private final GameMode gm;
    private Camera camera;
    private CameraGroup group;
    private EntityPlayer npc;
    private final Location loc;

    public Viewer(Player p, Camera camera, CameraGroup group) {
        super(p.getUniqueId().toString());
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

    public EntityPlayer getNpc() {
        return npc;
    }
    public void setNpc(EntityPlayer npc) {
        this.npc = npc;
    }

    public Location getLoc() {
        return loc;
    }
}
