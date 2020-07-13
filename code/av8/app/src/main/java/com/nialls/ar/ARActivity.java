package com.nialls.ar;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.nialls.R;
import com.nialls.R.id;
import com.nialls.app.Plane;
import com.nialls.app.SnippetFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.LocationMarker.ScalingMode;

import static com.nialls.ar.ARHelper.scaleModifier;

public final class ARActivity extends AppCompatActivity implements SensorEventListener {

    private FirebaseFunctions mFunctions;

    private Map<String, String> json;
    private String jsonString;

    // Set to prevent duplicate plane markers
    private Set<Plane> aircraft = new HashSet<>();

    private boolean arReqInstall;
    private int apiRequestNum = 0;

    private LocationScene locationScene;

    public LocationScene getLocationScene() {
        return locationScene;
    }
    private ArSceneView arSceneView;

    private Location userLocation = Location.NULL_LOCATION;

    private Handler arHandler = new Handler(Looper.getMainLooper());

    private SensorManager mSensorManager;
    private Sensor mRotatationSensor;
    private TextView compassHeading;

    private final Runnable resumeArElementsTask = (new Runnable() {
        public final void run() {
            if (locationScene != null) {
                locationScene.resume();
            }
            try {
                arSceneView.resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
        }
    });

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.arSceneView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mRotatationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        compassHeading = findViewById(R.id.compassHeading);
    }

    protected void onResume() {
        super.onResume();
        checkPermissions();
        mSensorManager.registerListener(this, mRotatationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        if (arSceneView.getSession() != null) {
            if (locationScene != null) {
                locationScene.pause();
            }
            arSceneView.pause();
        }
        mSensorManager.unregisterListener(this, mRotatationSensor);
    }

    // Sets-up AR session
    private void setupSession() {
        Toast loading = Toast.makeText(this, "Please wait, markers are loading.\nTap a marker for more information", Toast.LENGTH_LONG);
        loading.setGravity(Gravity.CENTER, 0, 0);
        loading.show();
        if (arSceneView == null) {
            return;
        }
        if (arSceneView.getSession() == null) {
            try {
                Session session = ARHelper.setupSession(this, arReqInstall);
                if (session == null) {
                    arReqInstall = true;
                    return;
                }
                else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                ARHelper.handleSessionException(this, e);
            }
        }

        // Creates new LocationScene & sets various parameters
        if (locationScene == null) {
            locationScene = new LocationScene(this, arSceneView);
            locationScene.setMinimalRefreshing(true);
            locationScene.setOffsetOverlapping(true);
            // Removes overlapping markers absolutely but can cause missing markers
            // locationScene.setRemoveOverlapping(true);
            locationScene.setAnchorRefreshInterval(5000);
        }

        try {
            resumeArElementsTask.run();
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Cannot access camera", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
            return;
        }

        // Retrieves user location by calling GetUserLocation
        if (userLocation.equals(Location.NULL_LOCATION)) {
            // Only allows API to be called once due to quota limits
            if (apiRequestNum == 0) {
                new GetUserLocation(new WeakReference(this)).execute(locationScene);
                apiRequestNum += 1;
            }
        }
    }

    // Calls ADS-B Exchange API via Firebase Cloud Server
    public void apiUpdate(Double userLat, Double userLon) {
        mFunctions = FirebaseFunctions.getInstance();

        Location userLocation = new Location(String.valueOf(userLat), String.valueOf(userLon));
        String latitude = userLocation.getLatitude();
        String longitude = userLocation.getLongitude();

        // Gets API distance from user preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String distance = preferences.getString("ARDist", "25");

        // [START apiCall]
        apiCall(latitude, longitude, distance)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        Log.w("Error:", "apiCall:onFailure", e);
                    }
                    // Creates string with JSON results (planes)
                    jsonString = new Gson().toJson(json);
                    Log.i("AR Aircraft:", jsonString);

                    try {
                        // Invokes method to parse string
                        parseJson(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        // [END apiCall]
    }

    private Task<String> apiCall(String lat, String lon, String dst) {
        // Create the arguments to the callable function, which are three strings
        Map<String, String> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lon);
        data.put("dist", dst);

        // Calls Firebase JavaScript function "apiCall" and extract the operation from the result
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

    private void parseJson(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        // Gets JSONArray of list of aircraft
        JSONArray arr = obj.getJSONArray("ac");

        // Creates a list of Plane objects
        for (int i = 0; i < arr.length(); i++) {
            aircraft.add(new Plane(arr.getJSONObject(i), "", ""));
        }
        // Calls first database method
        lookupAicraftName(aircraft);
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
                                } catch (Exception e) {
                                    Log.d("Exception:", "No aircraft ICAO code available");
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
                                    // If ICAO code is found, the full airline name is set (ICAO code first 3 letters of callsign)
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
                        createPlanes();
                    }
                }
            }
        });
    }

    void createPlanes() {
        renderPlaneObjects();
        updatePlaneObjects();
    }

    // Renders the markers representing Plane objects
    private void renderPlaneObjects() {
        for (Plane plane : aircraft) {
            CompletableFuture renderable = ViewRenderable.builder()
                    .setView(this, R.layout.renderable)
                    .build();
            CompletableFuture.anyOf(renderable)
                    .handle(
                            (notUsed, throwable) -> {
                                if (throwable != null) {
                                    return null;
                                }
                                else {
                                    try {
                                        // Creates LocationMarker for each set of aircraft co-ordinates
                                        LocationMarker planeMarker = new LocationMarker(
                                                Double.parseDouble(plane.getLon()),
                                                Double.parseDouble(plane.getLat()),
                                                setPlaneNode(plane, renderable)
                                        );
                                        addPlaneToARScene(
                                                planeMarker,
                                                ((ViewRenderable)renderable.get()).getView());
                                    } catch (Exception e) {
                                        // Continue
                                    }
                                    return null;
                                }
                            });
        }
    }

    // Updates plane markers and sets height
    private void updatePlaneObjects() {
        arSceneView.getScene().addOnUpdateListener(it -> {
            if (locationScene != null) {
                ArrayList<LocationMarker> markers = locationScene.mLocationMarkers;
                for(Object marker : markers) {
                    LocationMarker locationMarker = (LocationMarker) marker;
                    // locationMarker.setHeight(10.0F);
                    if (locationMarker.anchorNode != null) {
                        // Generates random height from ARHelper class function
                        locationMarker.setHeight(ARHelper.heightGenerator(
                                locationMarker.anchorNode.getDistance()));
                    }
                }
            }

            Frame frame = arSceneView.getArFrame();
            if (frame == null) {
                return;
            }
            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                return;
            }
            if (locationScene != null) {
                locationScene.processFrame(frame);
            }
        });
    }

    // Adds the Plane location marker to the AR scene
    private void addPlaneToARScene(final LocationMarker locationMarker, final View layoutRendarable) {

        // Sets initial scaling settings
        locationMarker.setScalingMode(ScalingMode.FIXED_SIZE_ON_SCREEN);
        locationMarker.setScaleModifier(0.5F);

        if (locationScene != null) {
            List<LocationMarker> markers = locationScene.mLocationMarkers;
            markers.add(locationMarker);
        }
        if (locationMarker.anchorNode != null) {
            locationMarker.anchorNode.setEnabled(true);
        }

        // Refresh markers
        this.arHandler.post(() -> {
            if (locationScene != null) {
                locationScene.refreshAnchors();
            }
            layoutRendarable.findViewById(id.pinContainer).setVisibility(View.VISIBLE);
        });

        // Sets distance (between plane and user) text view
        locationMarker.setRenderEvent(locationNode -> {
            AppCompatTextView distText = layoutRendarable.findViewById(id.distance);
            distText.setText(ARHelper.showDistance(locationNode.getDistance()));

            this.planeScaler(locationMarker, locationNode.getDistance());
        });
    }

    // Scales the markers based on distance to reduce clutter
    private void planeScaler(LocationMarker locationMarker, int distance) {
        float scaleModifier = scaleModifier(distance);
        // float scaleModifier = 0.25F;
        if (scaleModifier == -1.0F) {
            detachMarker(locationMarker);
        } else {
            locationMarker.setScaleModifier(scaleModifier);
        }
    }

    // Removes marker from location scene
    private void detachMarker(LocationMarker locationMarker) {
        if (locationMarker.anchorNode != null) {
            Objects.requireNonNull(locationMarker.anchorNode.getAnchor()).detach();
            locationMarker.anchorNode.setEnabled(false);
        }
        locationMarker.anchorNode = null;
    }

    // Sets Plane marker text
    private Node setPlaneNode(final Plane p, CompletableFuture completableFuture) throws ExecutionException, InterruptedException {
        Node node = new Node();
        node.setRenderable((Renderable) completableFuture.get());

        View nodeLayout = ((ViewRenderable)completableFuture.get()).getView();

        AppCompatTextView nameText = nodeLayout.findViewById(id.name);

        // Label text varies depending on available information from API
        if (!p.getAirlineName().equals("")) {
            nameText.setText(p.getAirlineName());
        }
        else if (!p.getCall().equals("")) {
            nameText.setText(p.getCall());
        }
        else {
            nameText.setText(p.getReg());
        }

        AppCompatTextView typeText = nodeLayout.findViewById(id.type);
        if (!p.getAircraftName().equals("")) {
            typeText.setText(p.getAircraftName());
        }
        else {
            typeText.setText(p.getType());
        }

        nodeLayout.findViewById(id.pinContainer).setVisibility(View.GONE);

        // Shows aircraft information if marker touched
        nodeLayout.setOnTouchListener((view, motionEvent) -> {
            String message = SnippetFormatter.format(p);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(17, 0, 0);
            toast.show();
            view.performClick();
            return false;
        });

        // Adds image to AR marker box
        Glide.with(this)
                .load("https://i.postimg.cc/J7QkdkRQ/plane-icon.png")
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into((ImageView)nodeLayout.findViewById(id.categoryIcon));
        return node;
    }

    // Ensures camera permission is granted
    private void checkPermissions() {
        if (!PermissionHelper.checkLocationCamera(this)) {
            PermissionHelper.requestLocationCamera(this);
        } else {
            setupSession();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] results) {
        if (!PermissionHelper.checkLocationCamera(this)) {
            Toast toast = Toast.makeText(this, "AR feature requires Location and Camera permissions", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            if (!PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                PermissionHelper.changePermissions(this);
            }
            finish();
        }
    }

    // This gets the users azimuth when the rotation sensor detects that user has changed orientation
    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] mRotation = event.values;

        float[] R = new float[16];
        SensorManager.getRotationMatrixFromVector(R, mRotation);

        float[] orientation = new float[3];
        SensorManager.getOrientation(R, orientation);

        float userDirection = Math.round(-((float) Math.toDegrees(orientation[0])));
        compassHeading.setText(String.valueOf(userDirection));

        // Log.i("AR Sensor Changed", "Orientation: " + userDirection);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }
}
