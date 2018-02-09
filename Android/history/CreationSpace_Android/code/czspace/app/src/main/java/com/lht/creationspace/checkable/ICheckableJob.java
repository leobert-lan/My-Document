package com.lht.creationspace.checkable;

/**
 * <p><b>Package</b> com.lht.vsocyy.checkable
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ICheckableJob
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/21.
 */

public interface ICheckableJob {
    /**
     * 检验合法
     */
    boolean RESULT_CHECK_LEGAL = true;

    /**
     * 检验非法
     */
    boolean RESULT_CHECK_ILLEGAL = false;

    boolean check();
}
