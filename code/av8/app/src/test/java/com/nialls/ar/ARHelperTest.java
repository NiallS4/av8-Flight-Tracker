package com.nialls.ar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ARHelperTest {

    @Test
    public void correctScale1() {
        assertEquals("Should be equal", ARHelper.scaleModifier(7000), 0.3F, 0.01);
    }

    @Test
    public void correctScale2() {
        assertEquals("Should be equal", ARHelper.scaleModifier(100000000), 0.2F, 0.01);
    }

    @Test
    public void incorrectScale1() {
        assertNotEquals("Should not be equal", ARHelper.scaleModifier(1), 0.4F, 0.01);
    }

    @Test
    public void incorrectScale2() {
        assertNotEquals("Should be equal", ARHelper.scaleModifier(-1), 0.2F, 0.01);
    }

    @Test
    public void correctHeight1() {
        Float randomNum = ARHelper.heightGenerator(3000);
        assertTrue("Should be between 3 and 5", 3 <= randomNum && randomNum <= 5);
    }

    // Test revealed error in height generator function
    @Test
    public void correctHeight2() {
        Float randomNum = ARHelper.heightGenerator(1);
        assertTrue("Should be between 1 and 2", 1 <= randomNum && randomNum <= 2);
    }

    @Test
    public void incorrectHeight() {
        Float randomNum = ARHelper.heightGenerator(100000);
        assertFalse("Should be between 3 and 5", 10 <= randomNum && randomNum <= 13);
    }
}