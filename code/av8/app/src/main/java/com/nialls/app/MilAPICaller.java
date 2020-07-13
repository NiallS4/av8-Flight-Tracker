package com.nialls.app;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.nialls.R;
import com.nialls.main.MainActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MilAPICaller extends APICaller {

    private FirebaseFunctions mFunctions;

    private Map<String, String> json;
    private String jsonString;

    // Calls ADS-B Exchange API via Firebase Cloud Server
    public void apiCall() {
        mFunctions = FirebaseFunctions.getInstance();

        // [START apiCall]
        firebaseCall()
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
                        setIsMilitary(true);
                        parseJson(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        // [END apiCall]
    }

    private Task<String> firebaseCall() {
        Map<String, String> data = new HashMap<>();

        // Calls Firebase JavaScript function "getMilitaryAircraft" and creates HashMap
        return mFunctions
                .getHttpsCallable("getMilitaryAircraft")
                .call(data)
                .continueWith(task -> {
                    // Creates a map of strings
                    @SuppressWarnings("unchecked")
                    Map<String, String> result = (Map<String, String>) Objects.requireNonNull(task.getResult()).getData();
                    json = result;
                    return null;
                });
    }

}