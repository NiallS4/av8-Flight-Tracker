package com.nialls.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PlaneUnitTest{

    @Test
    public void sampleTest(){
        int i = 3;
        int j = 5;
        assertEquals("failure - should equal 8", i + j, 8);
    }

    @Test
    public void validLatitudeTest1(){
        assertTrue("failure - should be true", Plane.isValidLatitude("54.520713"));
    }

    @Test
    public void validLatitudeTest2(){
        assertFalse("failure - should be false",Plane.isValidLatitude("148920.000000"));
    }

    @Test
    public void validLongitudeTest1(){
        assertTrue("failure - should be true",Plane.isValidLongitude("125.000763"));
    }

    @Test
    public void validLongitudeTest2(){
        assertFalse("failure - should be false",Plane.isValidLongitude("-9900240.111111"));
    }
}