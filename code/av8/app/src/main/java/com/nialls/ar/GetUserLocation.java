package com.nialls.ar;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import uk.co.appoly.arcorelocation.LocationScene;;

public final class GetUserLocation extends AsyncTask<LocationScene, Void, List<Double>> {

    private final WeakReference<ARActivity> ref;

    GetUserLocation(WeakReference<ARActivity> weakReference) {
        ref = weakReference;
    }

    // Returns user current location based on device co-ordinates
    @Override
    public List<Double> doInBackground(LocationScene[] l) {
        Double userLat = null;
        Double userLon = null;

        do { // Iterates until device location is obtained (not null)
            try {
                userLat = l[0].deviceLocation.currentBestLocation.getLatitude();
                userLon = l[0].deviceLocation.currentBestLocation.getLongitude();
                Log.i("UserLat:", String.valueOf(userLat));
                Log.i("UserLong:", String.valueOf(userLon));
            } catch (NullPointerException e) {
                // continue
            }
        } while(userLat == null || userLon == null);

        List<Double> latlng = new ArrayList<>();
        latlng.add(userLat);
        latlng.add(userLon);

        return latlng;
    }

    @Override
    protected void onPostExecute(List<Double> location) {
        // Calls apiUpdate method with user lat/lon on completion
        ref.get().apiUpdate(((Number)location.get(0)).doubleValue(), ((Number)location.get(1)).doubleValue());
        super.onPostExecute(location);
    }

}