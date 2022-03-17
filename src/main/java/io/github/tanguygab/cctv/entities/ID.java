package io.github.tanguygab.cctv.entities;

import io.github.tanguygab.cctv.managers.Manager;

public class ID {

    private String id;
    private final Manager<?> manager;

    public ID(String id, Manager<?> manager) {
        this.id = id;
        this.manager = manager;
    }

    protected void save() {}
    protected void set(String path, Object value) {
        manager.file.set(getId()+"."+path,value);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        manager.file.set(this.id,null);
        manager.delete(this.id);
        this.id = id;
        save();
        manager.put(id,this);

    }
}
