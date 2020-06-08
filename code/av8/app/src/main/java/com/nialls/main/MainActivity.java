package com.nialls.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.nialls.R;
import com.nialls.app.APICaller;
import com.nialls.app.Filter;
import com.nialls.app.IconChooser;
import com.nialls.app.MilAPICaller;
import com.nialls.app.RegAPICaller;
import com.nialls.ar.ARActivity;
import com.nialls.app.Animations;

import static com.nialls.app.IconChooser.changeToCurrentIcon;
import static com.nialls.app.IconChooser.changeToDefaultIcon;

public class MainActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        SensorEventListener {

    private static MainActivity instance;
    private GoogleMap mMap;
    // private int mapType = GoogleMap.MAP_TYPE_TERRAIN;

    // Compass sensor manager and needle that will animate user direction
    private SensorManager mSensorManager;
    private ImageView compass;
    private ImageView compassNeedle;
    // Sensors and sensor values for calculating bearing
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;

    // Values store user direction and allow tweaking of compass update sensitivity
    private float userDirection = 0f;
    private float previousDirection = 0f;

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 2;

    private View aircraftInfoBox;
    private boolean aircraftInfoBoxHidden;
    private boolean aircraftInfoBoxExpanded;
    private TextView aircraftInfoTitle;
    private TextView aircraftInfoSnippet;
    private Marker previousMarker;

    APICaller api = new APICaller();
    MilAPICaller mil = new MilAPICaller();
    RegAPICaller reg = new RegAPICaller(this);
    Filter filter = new Filter(this);

    private FusedLocationProviderClient fusedLocationClient;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        instance = this;

        // Initialises map fragment
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Compass code
        compass = findViewById(R.id.compass);
        compassNeedle = findViewById(R.id.compassNeedle);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = Objects.requireNonNull(mSensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int compassVis = preferences.getInt("CompassVis", View.GONE);
        compass.setVisibility(compassVis);
        compassNeedle.setVisibility(compassVis);
    }

    // Request permissions from user
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        // Location permission
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            // If user does not grant location permission, map defaults to Dublin
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(this, "Location permission required to use AR", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                LatLng dubAirport = new LatLng(53.427, -6.245);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dubAirport, 8.0f));
                api.apiCall(String.valueOf(dubAirport.latitude), String.valueOf(dubAirport.longitude));
            }
            // If user grants location permission
            else {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationClickListener(this);
                // Get user current location
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double userLat = location.getLatitude();
                                double userLon = location.getLongitude();
                                LatLng userLoc = new LatLng(userLat, userLon);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 8.0f));
                                // If user location granted, show aircraft at user location
                                api.apiCall(String.valueOf(userLat), String.valueOf(userLon));
                            }
                        });
            }
        }
        // Camera permission
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.CAMERA) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent ar = new Intent(this, ARActivity.class);
                startActivity(ar);
            } else {
                Toast toast = Toast.makeText(this, "Camera permission required to use AR", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners for compass
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners to save battery
        mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void searchByReg(Menu menu) {
        // Adds search functionality to find plane by registration
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        icon.setColorFilter(Color.WHITE);

        searchView.setQueryHint("Enter a reg");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);
                // Call API when search is submitted
                reg.apiCall(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
    }

    // Creates menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        searchByReg(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initialises Google Map
        mMap = googleMap;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int mapType = preferences.getInt("MapType", GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMapType(mapType);

        mMap.getUiSettings().setRotateGesturesEnabled(false); // Map rotation disabled as it affects plane and compass bearings
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);

        // Ensures that location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationClickListener(this);

            // Get user current location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double userLat = location.getLatitude();
                            double userLon = location.getLongitude();
                            LatLng userLoc = new LatLng(userLat, userLon);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 8.0f));
                            // If user location granted, show aircraft at user location
                            api.apiCall(String.valueOf(userLat), String.valueOf(userLon));
                        }
                    });
        }
        // If user location not granted, show aircraft at fixed coordinates (Dublin Airport)
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }

        // Set up aircraft info box
        aircraftInfoBox = findViewById(R.id.aircraftInfoBox);
        aircraftInfoTitle = findViewById(R.id.aircraftInfoTitle);
        aircraftInfoSnippet = findViewById(R.id.airCraftInfoSnippet);

        // Init info box off screen
        aircraftInfoBoxHidden = true;
        aircraftInfoBoxExpanded = false;
        Animations.initialOffScreen(aircraftInfoBox);
    }

    // Show information when user taps on their location
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String message = "Current location:" +
                "\nLatitude: " + location.getLatitude() +
                "\nLongitude: " + location.getLongitude() +
                "\nAltitude: " + location.getAltitude() + " metres" +
                "\nBearing: " + location.getBearing() + "\u00B0";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Custom On aircraft icon click method to allow for aircraft info box in bottom left corner rather than directly over aircraft icon
    // TODO Instead of turning icon black after tap, change it back to original colour
    @Override
    public boolean onMarkerClick(final Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);

        // if user taps on new aircraft & there is no currently selected aircraft, update values and animate infobox into users vision
        if (aircraftInfoBoxHidden) {
            if (previousMarker == null){
                previousMarker = marker;
            }

//            Log.i("Aircraft Tap: ", marker.getTitle() + " " + previousMarker.getTitle());

            changeToCurrentIcon(marker); // Change colour of selected icon to highlight to user

            aircraftInfoTitle.setText(marker.getTitle());
            aircraftInfoSnippet.setText(marker.getSnippet());

            Animations.onScreen(aircraftInfoBox);

            previousMarker = marker;
            aircraftInfoBoxHidden = false;
        }

        // if user taps on another aircraft icon, update the aircraft details
        else if (!marker.getTitle().equals(previousMarker.getTitle())){
            changeToDefaultIcon(previousMarker);
            changeToCurrentIcon(marker);

            aircraftInfoTitle.setText(marker.getTitle());
            aircraftInfoSnippet.setText(marker.getSnippet());

            previousMarker = marker;
            aircraftInfoBoxHidden = false;
        }

        // else if user taps on the currently selected aircraft icon, move the info box off screen // && marker == previousMarker
        else{
            changeToDefaultIcon(marker);

            Animations.offScreen(aircraftInfoBox);

            previousMarker = marker;
            aircraftInfoBoxHidden = true;
        }

        // return true to clear default onClick behaviour
        return true;
    }

    // Expand or collapse info box when user taps on it
    public void onInfoBoxClick (View view) {
        if (!aircraftInfoBoxExpanded) {
            Animations.expandBox(aircraftInfoBox);
            aircraftInfoBoxExpanded = true;
        }
        else {
            Animations.collapseBox(aircraftInfoBox);
            aircraftInfoBoxExpanded = false;
        }
    }

    // Controls when user taps button on menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        switch (item.getItemId()) {

            case R.id.action_camera:
                // User chose the "AR Camera" action, open the camera
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent ar = new Intent(this, ARActivity.class);
                    startActivity(ar);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }
                return true;

            case R.id.action_refresh:
                // Clear info box
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                LatLng mapCentreLoc = MainActivity.getInstance().getmMap().getCameraPosition().target;
                String centreLat = String.valueOf(mapCentreLoc.latitude);
                String centreLon = String.valueOf(mapCentreLoc.longitude);

                api.apiCall(centreLat, centreLon); // Calls the API again
                return true;

            case R.id.filter_type:
                // Clear info box
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                filter.filterByType();
                return true;

            case R.id.filter_operator:
                // Clear info box
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                filter.filterByOperator();
                return true;

            case R.id.filter_country:
                // Clear info box
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                filter.filterByCountry();
                return true;

            case R.id.show_compass:
                editor.putInt("CompassVis", View.VISIBLE);
                editor.apply();
                compass.setVisibility(View.VISIBLE);
                compassNeedle.setVisibility(View.VISIBLE);
                return true;

            case R.id.hide_compass:
                editor.putInt("CompassVis", View.GONE);
                editor.apply();
                compass.setVisibility(View.GONE);
                compassNeedle.setVisibility(View.GONE);
                return true;

            case R.id.set_map_terrain:
                editor.putInt("MapType", GoogleMap.MAP_TYPE_TERRAIN);
                editor.apply();
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            case R.id.set_map_normal:
                editor.putInt("MapType", GoogleMap.MAP_TYPE_NORMAL);
                editor.apply();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.set_map_satellite:
                editor.putInt("MapType", GoogleMap.MAP_TYPE_SATELLITE);
                editor.apply();
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.set_map_hybrid:
                editor.putInt("MapType", GoogleMap.MAP_TYPE_HYBRID);
                editor.apply();
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.set_ar_25nm:
                editor.putString("ARDist", "25");
                editor.apply();
                return true;

            case R.id.set_ar_100nm:
                editor.putString("ARDist", "100");
                editor.apply();
                return true;

            case R.id.show_military:
                // Clear info box
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                mil.apiCall();
                return true;

            case R.id.show_altitude_heat_map:
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                IconChooser.colourIconsByAltitude(APICaller.aircraftIcons);
                return true;

            case R.id.show_speed_heat_map:
                aircraftInfoBoxHidden = true;
                Animations.offScreen(aircraftInfoBox);

                IconChooser.colourIconsBySpeed(APICaller.aircraftIcons);
                return true;

            case R.id.help:
                showHelpDialog();
                return true;

            default:
                // Invokes the superclass if user action not recognised
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage(
                        "Tap a plane icon to view basic information.\n" +
                        "\nFor full details, tap the information box to expand it. To close it, tap the selected icon.\n" +
                        "\nTo refresh/reset the map, use the refresh button on the app bar. This may take up to 5 seconds.\n" +
                        "\nThe search function allows any aircraft to be located by its registration (e.g. 'EI-DVM').\n" +
                        "\nThe filter option allows planes to be filtered by aircraft type, airline and country.\n" +
                        "\nThe altitude heat map ranges from high to low – red to yellow.\n" +
                        "\nThe altitude heat map ranges from high to low – red to yellow.\n" +
                        "\nThe AR feature requires your device to support Google Play Services for AR."
                )
                .setPositiveButton("OK", (dialog, which) -> {
                    // Close dialog
                })
                .show();
    }

    // When accelerometer and magnetometer listeners detect change, calculate users new bearing
    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.e("Sensor Changed: ", "Failed Successfully!");

        // Get accelerometer values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        // Get magnetometer values
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        // If sensor values are not null, create rotation matrix R and inclination matrix I
        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation float array will contain 3 values: azimuth, pitch and roll. All are in radians
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                // In this case we want to get the azimuth as the userDirection
                userDirection = -((float) Math.toDegrees(orientation[0]));
                float trueNorth = 360.0f - userDirection; // This is a test value. should always be 0.0
            }
        }
//        Log.i("Sensor Changed", "Orientation: " + userDirection);
//        Log.i("Sensor Changed", "North: " + trueNorth);

        // if change in rotation is greater than 1 degree, rotate the compass
        float deltaR = userDirection - previousDirection;
        if (Math.abs(deltaR) > 4) {
            compass.setRotation(userDirection);
        }

        previousDirection = userDirection;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Pass
    }
}
