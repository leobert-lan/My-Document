package com.lht.creationspace.base.domain.interactors;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.interactors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> Interactor </p>
 * <p><b>Description:</b> Interface to define a LongTime job run at backend </p>
 * Created by leobert on 2017/8/4.
 */

interface Interactor {
    /**
     * starts the interactor.
     */
    void execute();
}
