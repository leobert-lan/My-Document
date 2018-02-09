package com.lht.creationspace.structure.lrtree;

/**
 * <p><b>Package:</b> com.lht.creationspace.structure.lrtree </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LRNodeWrapper </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/5/15.
 */

public abstract class LRNodeWrapper<D> {
    private final D data;

    public LRNodeWrapper(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }

    public abstract int getRoot();

    public abstract int getLeft();

    public abstract int getRight();

    public abstract int getLvl();

}
