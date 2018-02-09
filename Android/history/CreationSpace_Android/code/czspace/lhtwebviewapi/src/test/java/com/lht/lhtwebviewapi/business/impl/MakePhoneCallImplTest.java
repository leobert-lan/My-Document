package com.lht.lhtwebviewapi.business.impl;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.lht.lhtwebviewapi.business.bean.PhoneNumBean;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chhyu on 2016/12/2.
 */
public class MakePhoneCallImplTest {
    private MakePhoneCallImpl makePhoneCall;
    @Before
    public void setUp() {
        makePhoneCall = new MakePhoneCallImpl(new Activity());
    }

    @Test
    public void isBeanError() throws Exception {
        PhoneNumBean testBean = new PhoneNumBean();
        testBean.setTelphone("123");

        assertEquals(true,makePhoneCall.isBeanError(testBean));

    }

}