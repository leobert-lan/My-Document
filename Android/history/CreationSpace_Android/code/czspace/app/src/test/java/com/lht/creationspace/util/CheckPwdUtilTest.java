package com.lht.creationspace.util;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.util </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> CheckPwdUtilTest </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/11/6.
 */
public class CheckPwdUtilTest {

    private IActivityAsyncProtected iActivityAsyncProtected;

    @Before
    public void setUp() {
    }


    @Test
    public void isPasswordLegal() throws Exception {
        List<String> illegalCases = Arrays.asList(
                null,
                "",
                "1",
                "1234567896",
                "ad\"234rfvwe",
                "aiudhfiavbhq28922i3urv"
                ,"1Cy-dsaf-2"
        );

        for (String s:illegalCases) {
            Assert.assertEquals(false,CheckPwdUtil.isPasswordLegal(s));
        }

        List<String> legalCases = Arrays.asList(
                "a12345",
                "a123456798798",
                "123dg1j",
                "dell_456",
                "!@#$%^&*()_+"//,
//                "/|{}[]?><,.-\\"
        );

        for (String s:legalCases) {
            Assert.assertEquals(true,CheckPwdUtil.isPasswordLegal(s));
        }

    }


}