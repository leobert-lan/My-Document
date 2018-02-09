package com.lht.cloudjob.mvp.model;

import org.junit.Test;

import static org.junit.Assert.*;


public class CheckVersionUpdateModelTest {
    @Test
    public void isUpdateNeeded() throws Exception {
        // 非法格式
        String remoteVer = "1.w";
        String currentVer = "1.1";
//        assertEquals(1,transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer,currentVer)));

        //本地长度更长
//        remoteVer = "1.1.2";
//        currentVer = "1.1.1.1";
//        assertEquals(1,transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer,currentVer)));

        //远端更长
//        remoteVer = "1.1.1.2";
//        currentVer = "1.1.1";
//        assertEquals(1,transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer,currentVer)));

        //当前比远端版本高
//        remoteVer = "1.1.1.2";
//        currentVer = "1.1.1.2";
//        assertEquals(1, transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer, currentVer)));

        //远端比当前版本高  true
//        remoteVer = "3.2.1.2";
//        currentVer = "2.1.1";
//        assertEquals(1, transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer, currentVer)));

        //版本一样
        remoteVer = "1.1.1.2";
        currentVer = "1.1.1.2";
        assertEquals(1, transBoolean(CheckVersionUpdateModel.isUpdateNeeded(remoteVer, currentVer)));
    }

    private int transBoolean(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }


}