package com.lht.creationspace.checkable.jobs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chhyu on 2017/4/7.
 */
public class NicknameCheckJobTest {
    private NicknameCheckJob checkJob;

    @Before
    public void setUp() throws Exception {
        checkJob = new NicknameCheckJob(null);
    }

    @Test
    public void check() throws Exception {
//        //1 null
//        assertEquals(false, checkJob.check());
//
//        //2 empty
//        checkJob.setNickname("");
//        assertEquals(false, checkJob.check());
//
//        //3 中文
//        checkJob.setNickname("中文");
//        assertEquals(true, checkJob.check());
//
//        //4 全空格
//        checkJob.setNickname("   ");
//        assertEquals(false, checkJob.check());
//
//        //5 特殊符号
//        checkJob.setNickname("abc@#你好");
//        assertEquals(false, checkJob.check());
//
//        //6正常情况
//        checkJob.setNickname("汉子abcABC  123");
//        assertEquals(true, checkJob.check());
//
//        //7超长
//        checkJob.setNickname("1212121212121212121212121212121");
//        assertEquals(false, checkJob.check());
//
//        //8表情
//        checkJob.setNickname(":)");
//        assertEquals(false, checkJob.check());
    }

}