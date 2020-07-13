package com.nialls.app;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.nialls.R;
import com.nialls.main.MainActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegAPICaller extends APICaller {

    private FirebaseFunctions mFunctions;
    private Context mContext;

    private Map<String, String> json;
    private String jsonString;

    public RegAPICaller(Context context) { mContext = context; }

    // Calls ADS-B Exchange API via Firebase Cloud Server
    public void apiCall(String reg) {
        mFunctions = FirebaseFunctions.getInstance();

        // [START apiCall]
        firebaseCall(reg)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        Log.e("Error:", "apiCall:onFailure", e);
                    }
                    // Creates string with JSON results (planes)
                    jsonString = new Gson().toJson(json);
                    Log.i("RegLookup:", jsonString);

                    try {
                        // Invokes method to parse string
                        setIsRegCall(true);
                        parseJson(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(mContext, "Plane not found. Make sure registration is in the format 'EI-DVM' or 'N280WN'", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
        // [END apiCall]
    }

    private Task<String> firebaseCall(String reg) {
        // Create the arguments to the callable function, which are three strings
        Map<String, String> data = new HashMap<>();
        data.put("registration", reg);

        // Calls Firebase JavaScript function "getAircraftByReg" and creates HashMap
        return mFunctions
                .getHttpsCallable("getAircraftByReg")
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
