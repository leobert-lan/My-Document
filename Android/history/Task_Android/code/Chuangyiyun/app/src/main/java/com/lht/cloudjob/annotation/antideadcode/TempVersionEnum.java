package com.lht.cloudjob.annotation.antideadcode;

/**
 * <p><b>Package</b> com.lht.cloudjob.annotation.antideadcode
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TempVersionEnum
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/22.
 */

public enum TempVersionEnum implements ITempVersionInfo {

    /**
     * used as a default and unknown enum item
     */
    UNKNOWN("unknown ver","unknown desc");

//    CHECK("to check","if useless, remove");

//    DEPRECATED("deprecated","temp tag");

//    /**
//     * version for promote activity,contains appComment features and comment sort&praise
//     */
//    V1019("1.0.19","version for promote activity,contains appComment features and comment sort&praise");

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
