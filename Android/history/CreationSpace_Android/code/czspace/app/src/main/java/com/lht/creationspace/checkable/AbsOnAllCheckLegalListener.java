package com.lht.creationspace.checkable;

/**
 * <p><b>Package</b> com.lht.creationspace.checkable
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> AbsOnAllCheckLegalListener
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/25.
 */
public abstract class AbsOnAllCheckLegalListener<T> implements CheckableJobs.OnAllCheckLegalListener {

    final T savedParam;

    public AbsOnAllCheckLegalListener(T savedParam) {
        this.savedParam = savedParam;
    }

    protected T getSavedParam() {
        return savedParam;
    }
}
