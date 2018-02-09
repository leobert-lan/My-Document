package com.lht.creationspace.annotation.antideadcode;

/**
 * <p><b>Package</b> com.lht.vsocyy.annotation.antideadcode
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> TempVersionEnum
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/22.
 */

public enum TempVersionEnum implements ITempVersionInfo {

    /**
     * used as a default and unknown enum item
     */
    UHKNOWN("unknown ver","unknown desc"),

    DEPRECATE("deprecate","deprecate on re-architecture");

    private final String versionCode;

    private final String versionDesc;

    TempVersionEnum(String versionCode, String versionDesc) {
        this.versionCode = versionCode;
        this.versionDesc = versionDesc;
    }

    @Override
    public String getVersionCode() {
        return versionCode;
    }

    @Override
    public String getVersionDesc() {
        return versionDesc;
    }
}
