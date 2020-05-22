package com.nialls.ar;

import android.app.Activity;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableException;

import java.util.Locale;
import java.util.Random;

class ARHelper {

    // Ensures that ARCore is installed or device is compatible
    static Session setupSession(Activity activity, boolean installRequested) throws UnavailableException {
        ArCoreApk.InstallStatus installStatus = ArCoreApk.getInstance().requestInstall(activity, !installRequested);
        switch (installStatus) {
            case INSTALL_REQUESTED:
                return null;
            case INSTALLED:
                //
            default:
                //
        }

        Session session = new Session(activity);
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        session.configure(config);
        return session;

    }

    // Handles ARCore compatibility issue
    static void handleSessionException(Activity activity, UnavailableException sessionException) {
        Toast.makeText(activity, "ARCore not installed/not supported", Toast.LENGTH_LONG).show();
    }

    // Changes marker scale based on distance from user
    static float scaleModifier(int distance) {
        if (distance <= 2) {
            return -1.0F;
        }
        else if (distance <= 1000) {
            return 0.4F;
        }
        else if (distance <= 5000) {
            return 0.35F;
        }
        else if (distance <= 10000) {
            return 0.3F;
        }
        else if (distance <= 25000) {
            return 0.25F;
        }
        else if (distance <= 50000) {
            return 0.2F;
        }
        else {
            return 0.2F;
        }
    }

    // Generates marker height based on distance from user
    static Float heightGenerator(int distance) {
        Random random = new Random();
        if (distance <= 1000) {
            return (float) (random.nextInt(2 - 1 + 1) + 1);
        }
        else if (distance <= 5000) {
            return (float) (random.nextInt(5 - 3 + 1) + 3);
        }
        else if (distance <= 10000) {
            return (float) (random.nextInt(6 - 4 + 1) + 4);
        }
        else if (distance <= 25000) {
            return (float) (random.nextInt(9 - 7 + 1) + 7);
        }
        else if (distance <= 50000) {
            return (float) (random.nextInt(13 - 10 + 1) + 10);
        }
        else {
            return (float) (random.nextInt(17 - 14 + 1) + 14);
        }
    }

    // Shows distance from user to plane in kilometres/metres
    static String showDistance(int distance) {
        String dist;
        if (distance >= 1000) {
            dist = String.format(Locale.UK, "%.2f km", ((float) distance / 1000));
        }
        else
            dist = distance + " m";
        return dist;
    }

}
