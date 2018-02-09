package com.lht.creationspace.structure;

import java.util.Stack;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> SingletonStack
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/28.
 */

public class SingletonStack<E> extends Stack<E> {

    @Override
    public synchronized boolean add(E object) {
        if (contains(object)) {
            remove(object);
        }

        return super.add(object);
    }
}
