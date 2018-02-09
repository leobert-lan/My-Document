package com.lht.creationspace.annotation.antideadcode;

/**
 * <p><b>Package</b> com.lht.vsocyy.annotation
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ITempVersionInfo
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/22.
 */

public interface ITempVersionInfo {
    /**
     * get the version code of the temp version
     * @return version code,e.g. "1.0.19"
     */
    String getVersionCode();

    /**
     * get the version description of the temp version
     * @return version description,e.g. "version for promote activity,contains appComment features and comment sort&praise"
     */
    String getVersionDesc();
}
