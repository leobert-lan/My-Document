package com.lht.cloudjob.customview;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CustomProgressViewTest
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/10.
 */
public class CustomProgressViewTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void calcProgress() throws Exception {
        assertEquals("正在加载(10%)",CustomProgressView.calcProgress(10,100));
    }

}