package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CameraGroup extends ID {

    private String owner;
    private final List<Camera> cameras;

    public CameraGroup(String name, String owner, List<String> cameras) {
        super(name,CCTV.get().getCameraGroups());
        setOwner(owner);

        this.cameras = new ArrayList<>();
        cameras.forEach(str->{
            Camera cam = CCTV.get().getCameras().get(str);
            if (cam != null) this.cameras.add(cam);
        });
        set("cameras", cameras.isEmpty() ? null : Utils.list(cameras));
    }

    @Override
    public void save() {
        setOwner(owner);
        set("cameras", cameras.isEmpty() ? null : Utils.list(cameras));
    }

    public void setOwner(String owner) {
        this.owner = owner;
        set("owner",owner);
    }
    public String getOwner() {
        return owner;
    }
    public List<Camera> getCameras() {
        return cameras;
    }
    public void saveCams() {
        set("cameras", cameras.isEmpty() ? null : Utils.list(cameras));
    }
    public void addCamera(Camera cam) {
        cameras.add(cam);
        saveCams();
    }
    public void removeCamera(Camera cam) {
        cameras.remove(cam);
        saveCams();
    }
}
