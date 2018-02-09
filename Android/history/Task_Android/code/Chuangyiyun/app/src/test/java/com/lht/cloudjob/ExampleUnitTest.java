package com.lht.cloudjob;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
        assertEquals("1/5",te1(1,5));
    }

    private String te1(int current,int max) {
       return String.format(Locale.ENGLISH,"%d/%d",current,max);
    }

}