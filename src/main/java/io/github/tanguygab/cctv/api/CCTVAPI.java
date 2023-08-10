package io.github.tanguygab.cctv.api;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.managers.CameraManager;
import io.github.tanguygab.cctv.managers.ComputerManager;
import io.github.tanguygab.cctv.managers.ViewerManager;
import org.bukkit.entity.Player;

public class CCTVAPI {

    private static CCTVAPI instance;
    private final CCTV cctv;
    private final CameraManager cm;
    private final ComputerManager cpm;
    private final ViewerManager vm;

    public CCTVAPI(CCTV cctv) {
        instance = this;
        this.cctv = cctv;
        cm = cctv.getCameras();
        cpm = cctv.getComputers();
        vm = cctv.getViewers();
    }

    public static CCTVAPI get() {
        return instance;
    }

    public void connectToCamera(Player p, String camera) {
        if (cm.exists(camera))
            cctv.getCameras().viewCamera(p, cm.get(camera), null);
    }

    public void connectToComputer(Player p, String computerName) {
        if (!cpm.exists(computerName)) return;
        Computer computer = cpm.get(computerName);
        if (!computer.getCameras().isEmpty())
            cctv.getCameras().viewCamera(p, computer.getCameras().get(0), computer);
    }

    public void cycleCamera(Player player, boolean previous) {
        if (vm.exists(player))
            vm.switchCamera(player,previous);
    }

    public void setCanExitCamera(Player player, boolean canExit) {
        if (vm.exists(player))
            vm.get(player).setCanExit(canExit);
    }

    public boolean canExitCamera(Player player) {
        return vm.exists(player) && vm.get(player).canExit();
    }

    public boolean canUseCamera(Player p, String camera) {
        return cm.get(camera).getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.camera.other");
    }

    public String currentCamera(Player player) {
        return vm.exists(player) ? vm.get(player).getCamera().getId() : null;
    }

    public String currentCameraComputer(Player player) {
        return vm.exists(player) ? vm.get(player).getComputer().getId() : null;
    }


}
