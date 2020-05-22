package com.nialls.main;

import com.nialls.app.APICaller;
import com.nialls.app.Plane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class APICallerTest {

    private APICaller api = new APICaller();

    @Before
    public void init() {
        api.apiCall("63.996298", "-22.623897");
    }


    @Test
    public void invalidCoordinates() {
        api.apiCall("", "");
        // Empty coordinate strings, therefore should be no planes in JSON array (null array of AC plus data)
        assertEquals(APICaller.getAircraft().size(), 1);
    }

    @Test
    public void testJsonParser() throws JSONException {
        Set<Plane> expectedSet = new HashSet<>();
        String jsonString = "{ ac: \n" +
                "   [ { postime: '1583349733581',\n" +
                "       icao: '4074C6',\n" +
                "       reg: 'G-LCYY',\n" +
                "       type: 'E190',\n" +
                "       wtc: '2',\n" +
                "       spd: '311.9',\n" +
                "       altt: '0',\n" +
                "       alt: '9200',\n" +
                "       galt: '9210',\n" +
                "       talt: '23008',\n" +
                "       lat: '53.500066',\n" +
                "       lon: '-6.385884',\n" +
                "       vsit: '1',\n" +
                "       vsi: '800',\n" +
                "       trkh: '0',\n" +
                "       ttrk: '',\n" +
                "       trak: '85.6',\n" +
                "       sqk: '4477',\n" +
                "       call: 'CFE4473',\n" +
                "       gnd: '0',\n" +
                "       trt: '5',\n" +
                "       pos: '1',\n" +
                "       mlat: '0',\n" +
                "       tisb: '0',\n" +
                "       sat: '0',\n" +
                "       opicao: 'CFE',\n" +
                "       cou: 'United Kingdom',\n" +
                "       mil: '0',\n" +
                "       interested: '0',\n" +
                "       dst: '7.72' } ] }";

        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("ac");
        for (int i = 0; i < arr.length(); i++) {
            // Creates a list of Plane objects
            JSONObject planeInstance = arr.getJSONObject(i);
            expectedSet.add(new Plane(planeInstance, "", ""));
        }

        Set<Plane> actualSet = api.parseJson(jsonString);

        String expectedIcao = null;
        String expectedReg = null;
        for(Plane p : expectedSet) {
            expectedIcao = p.getIcao();
            expectedReg = p.getReg();
        }

        String actualIcao = null;
        String actualReg = null;
        for(Plane p : actualSet) {
            actualIcao = p.getIcao();
            actualReg = p.getReg();
        }

        assertEquals("Should be equal", expectedIcao, actualIcao);
        assertEquals("Should be equal", expectedReg, actualReg);
    }

    @Test
    public void getAircraft() {
    }

    @Test
    public void apiCall() {
    }

}