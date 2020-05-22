package com.nialls.main;

import com.nialls.app.IconChooser;

import org.junit.Test;

import static android.graphics.Color.rgb;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IconChooserTest {

    @Test
    public void correctAltColour1() {
        assertEquals("Wrong colour, should be (255, 255, 0)", IconChooser.altitudeToColour(0), rgb(255, 255, 0));
    }

    @Test
    public void correctAltColour2() {
        assertEquals("Wrong colour, should be (180, 0, 0)", IconChooser.altitudeToColour(65000), rgb(180, 0, 0));
    }

    @Test
    public void correctAltColour3() {
        assertEquals("Wrong colour, should be (255, 65, 64)", IconChooser.altitudeToColour(30000), rgb(255, 65, 64));
    }

    @Test
    public void incorrectAltColour() {
        assertNotEquals("Wrong colour, should not be (255, 255, 0)", IconChooser.altitudeToColour(30000), rgb(255, 255, 0));
    }

    @Test
    public void correctSpeedColour1() {
        assertEquals("Wrong colour, should be (64, 64, 255)", IconChooser.speedToColour(0), rgb(64, 64, 255));
    }

    @Test
    public void correctSpeedColour2() {
        assertEquals("Wrong colour, should be (190, 0, 128)", IconChooser.speedToColour(700), rgb(190, 0, 128));
    }

    @Test
    public void incorrectSpeedColour() {
        assertNotEquals("Wrong colour, should not be (180, 0, 128)", IconChooser.speedToColour(180), rgb(180, 0, 128));
    }
}
