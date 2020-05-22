package com.nialls.main;

import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.google.ar.sceneform.ArSceneView;
import com.nialls.R;
import com.nialls.ar.ARActivity;
import com.nialls.ar.Location;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import uk.co.appoly.arcorelocation.LocationScene;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ARActivityTest {

    @Rule
    public ActivityTestRule<ARActivity> mTestRule = new ActivityTestRule<>(ARActivity.class);

    private ARActivity arActivity = null;

    @Before
    public void before() throws Exception {
        arActivity = mTestRule.getActivity();
    }

    // Tests MainActivity to ensure that all elements are instantiated
    @Test
    public void testARSetup() {
        ArSceneView arSceneView = arActivity.findViewById(R.id.arSceneView);
        assertNotNull("AR Scene View should not be null:", arSceneView);

        TextView compassHeading = arActivity.findViewById(R.id.compassHeading);
        assertNotNull("Compass heading should not be null:", compassHeading);

        LocationScene locationScene = arActivity.getLocationScene();
        assertNotNull("Location scene should not be null:", locationScene);
    }

    @After
    public void after() throws Exception {
        arActivity = null;
    }
}