package com.nialls.ar;

import android.Manifest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PermissionHelperTest {

    @Test
    public void checkPermissionGranted() {
        assertEquals("Camera permission not granted", PermissionHelper.CAMERA_PERMISSION, Manifest.permission.CAMERA);
    }

    @Test
    public void checkLocationCamera() {
    }

}