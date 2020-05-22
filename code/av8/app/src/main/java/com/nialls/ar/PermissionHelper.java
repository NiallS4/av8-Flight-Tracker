package com.nialls.ar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

class PermissionHelper {

    private static final int MULTIPLE_PERMISSION_CODE = 1;
    static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};

    // Checks if location and camera permissions are granted
    static boolean checkLocationCamera(Activity activity) {
        for(String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Requests permissions if needed
    static void requestLocationCamera(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, permissions, MULTIPLE_PERMISSION_CODE);
    }

    // Shows why permissions are needed
    static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
    }

    // Opens permissions request activity
    static void changePermissions(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}