package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.CCTV;

import java.util.ArrayList;
import java.util.List;

public class CameraGroup extends ID {

    private String owner;
    private final List<Camera> cameras;

    public CameraGroup(String name, String owner, List<String> cameras) {
        super(name);
        this.owner = owner;

        this.cameras = new ArrayList<>();
        cameras.forEach(str->{
            Camera cam = CCTV.get().getCameras().get(str);
            if (cam != null) this.cameras.add(cam);
        });
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getOwner() {
        return owner;
    }
    public List<Camera> getCameras() {
        return cameras;
    }
}
