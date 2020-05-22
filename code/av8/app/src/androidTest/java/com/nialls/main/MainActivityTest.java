package com.nialls.main;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.nialls.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void before() throws Exception {
        mainActivity = mTestRule.getActivity();
    }

    // Tests MainActivity to ensure that all elements are instantiated
    @Test
    public void testAppOpen() {
        SupportMapFragment mapFragment = (SupportMapFragment) mainActivity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assertNotNull(mapFragment);
        GoogleMap mMap = mainActivity.getmMap();
        assertNotNull("Map should not be null:", mMap);

        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
        assertNotNull("Toolbar should not be null:", toolbar);

        ImageView compass = mainActivity.findViewById(R.id.compass);
        ImageView compassNeedle = mainActivity.findViewById(R.id.compassNeedle);
        assertNotNull("Compass should not be null:", compass);
        assertNotNull("Compass needle should not be null:", compassNeedle);

        View aircraftInfoBox = mainActivity.findViewById(R.id.aircraftInfoBox);
        assertNotNull("Infobox should not be null:", aircraftInfoBox);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);
        assertNotNull("Location client should not be null:", fusedLocationClient);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        assertNotNull("Preferences should not be null:", preferences);

    }

    @After
    public void after() throws Exception {
        mainActivity = null;
    }
}
