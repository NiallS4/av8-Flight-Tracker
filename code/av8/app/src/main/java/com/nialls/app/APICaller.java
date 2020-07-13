package com.nialls.app;

import androidx.annotation.NonNull;

import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.nialls.R;
import com.nialls.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.graphics.Color.rgb;

public class APICaller {

    private FirebaseFunctions mFunctions;
    private GoogleMap mMap;

    private Map<String, String> json;
    private String jsonString;

    private boolean isMilitary = false;
    private boolean isRegCall = false;

    private static Set<Plane> aircraft = new HashSet<>();

    public static Set<Plane> getAircraft() {
        return aircraft;
    }

    void setIsMilitary(boolean isMilitary) { this.isMilitary = isMilitary; }

    void setIsRegCall(boolean isRegCall) { this.isRegCall = isRegCall; }

    public static ArrayList<Marker> aircraftIcons = new ArrayList<>();

    // Calls ADS-B Exchange API via Firebase Cloud Server
    public void apiCall(String latitude, String longitude) {
        mFunctions = FirebaseFunctions.getInstance();

        String distance = "100"; // API only accepts distances of 1, 5, 10, 25 or 100nm

        // [START apiCall]
        firebaseCall(latitude, longitude, distance)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        Log.e("Error:", "apiCall:onFailure", e);
                    }
                    // Creates string with JSON results (planes)
                    jsonString = new Gson().toJson(json);
                    Log.i("Map Aircraft:", jsonString);

                    try {
                        // Invokes method to parse string
                        parseJson(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        // [END apiCall]
    }

    private Task<String> firebaseCall(String lat, String lon, String dst) {
        // Create the arguments to the callable function, which are three strings
        Map<String, String> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lon);
        data.put("dist", dst);

        // Calls Firebase JavaScript function "apiCall" and creates HashMap
        return mFunctions
                .getHttpsCallable("apiCall")
                .call(data)
                .continueWith(task -> {
                    // Creates a map of strings
                    @SuppressWarnings("unchecked")
                    Map<String, String> result = (Map<String, String>) Objects.requireNonNull(task.getResult()).getData();
                    json = result;
                    return null;
                });
    }

    public Set<Plane> parseJson(String jsonString) throws JSONException {
        getAircraft().clear();

        JSONObject obj = new JSONObject(jsonString);
        // Gets JSONArray of list of aircraft
        JSONArray arr = obj.getJSONArray("ac");

        // Creates a list of Plane objects
        for (int i = 0; i < arr.length(); i++) {
            aircraft.add(new Plane(arr.getJSONObject(i), "", ""));
        }
        // Calls first database method
        lookupAicraftName(aircraft);

        return aircraft;
    }

    // Retrieves full aircraft name from database. More user-friendly than ICAO code
    private void lookupAicraftName(Set<Plane> aircraft) {
        // Looks up aircraft database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("aircraft").document("24MfkwHXUr0JJY2uWnb5").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        Map<String, Object> map = document.getData();
                        for(Plane p : aircraft) {
                            for (Map.Entry<String, Object> entry : Objects.requireNonNull(map).entrySet()) {
                                // If type is found, the full aircraft type name is set
                                try {
                                    if (entry.getKey().equals(p.getType())) {
                                        p.setAircraftName(String.valueOf(entry.getValue()));;
                                    }
                                } catch (StringIndexOutOfBoundsException e ) {
                                    Log.e("Exception:", "No aircraft ICAO code available");
                                }
                            }
                        }
                        // Calls second database method
                        lookupAirlineName(aircraft);
                    }
                }
            }
        });
    }

    // Retrieves full airline name from database. More user-friendly than ICAO code
    private void lookupAirlineName(Set<Plane> aircraft) {
        // Looks up airline database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("airline").document("lwQWka6rr11EwU2UZ2EC").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        Map<String, Object> map = document.getData();
                        for(Plane p : aircraft) {
                            if (!p.getCall().equals(""))
                                for (Map.Entry<String, Object> entry : Objects.requireNonNull(map).entrySet()) {
                                    // If ICAO code is found, the full airline name is set (ICAO code first 3 letters of callsign
                                    try {
                                        if (entry.getKey().equals(p.getOpicao()) || entry.getKey().equals(p.getCall().substring(0, 3))) {
                                            p.setAirlineName(String.valueOf(entry.getValue()));;
                                        }
                                    } catch (StringIndexOutOfBoundsException e) {
                                        Log.e("Database error:", String.valueOf(e));
                                    }
                                }
                        }
                        // Calls method to render plane icons on map
                        mapUpdate(aircraft);
                    }
                }
            }
        });
    }

    // Method to render plane icons on map
    private void mapUpdate(Set<Plane> aircraft) {
        // Clears map
        aircraftIcons.clear();
        mMap = MainActivity.getInstance().getmMap();
        mMap.clear();

        for(Plane p : aircraft) {
            if(isMilitary) {
                p.setColour(rgb(80, 105, 0));
            }
            String snippet = SnippetFormatter.format(p);
            // Check if co-ordinates are valid before plotting on the map
            if(p.isValidPlane()) {
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon())))
                        .title(p.getIcao())
                        .icon(IconChooser.initialAircraftIcon(p))
                        .snippet(snippet);

                // Rotates marker based on aircraft heading (bearing)
                if (!p.getTrak().equals("")) {
                    marker.rotation(Float.parseFloat(p.getTrak()));
                }
                else {
                    marker.rotation((float) 0.0);
                }
                // Adds marker to map while adding marker to array list and binding plane instance to marker
                aircraftIcons.add(MainActivity.getInstance().getmMap().addMarker(marker));
                aircraftIcons.get(aircraftIcons.size() - 1).setTag(p);
                if(isRegCall) {
                    LatLng planeLoc = new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(planeLoc, 8.0f));
                }
            }
            else {
                Log.e("Invalid_Plane", p.toString());
            }
        }
        // Restore to default values
        setIsMilitary(false);
        setIsRegCall(false);
    }
}