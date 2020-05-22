package com.nialls.app;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.rgb;

// This class is for instantiating aircraft in the app.
// It keeps track of the plane's values, and has some methods for checking validity and converting to different unit types.
public class Plane {
    private String lat;
    private String lon;
    private String reg;
    private String type;
    private String call;
    private String alt;
    private String metricAlt;
    private String spd;
    private String metricSpd;
    private String trak;
    private String icao;
    private String opicao;
    private String country;
    private String aircraftName;
    private String airlineName;
    private int colour;

    // Creates Plane object based on information from API (JSONObject) and database calls
    public Plane(JSONObject planeInstance, String aircraftName, String airlineName) throws JSONException {
        this.lat = planeInstance.getString("lat");
        this.lon = planeInstance.getString("lon");
        this.reg = planeInstance.getString("reg");
        this.type = planeInstance.getString("type");
        this.call = planeInstance.getString("call");
        this.alt = planeInstance.getString("alt");
        this.metricAlt = planeInstance.getString("alt");
        this.spd = planeInstance.getString("spd");
        this.metricSpd = planeInstance.getString("spd");
        this.trak = planeInstance.getString("trak");
        this.icao = planeInstance.getString("icao");
        this.opicao = planeInstance.getString("opicao");
        this.country = planeInstance.getString("cou");
        this.aircraftName = aircraftName;
        this.airlineName = airlineName;
        this.colour = rgb(0, 0, 0); // default colour - used for setting the icon's default colour in the UI. unless specified otherwise, this will be black
    }

    @NotNull
    @Override
    public String toString () {
        return "Plane - ICAO = " + this.icao + ", Registration = " + this.reg + ", Type = " + this.type;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getReg() {
        return reg;
    }

    public String getType() {
        return type;
    }

    public String getCall() {
        return call;
    }

    String getAlt() {
        return alt;
    }

    String getSpd() {
        return spd;
    }

    String getTrak() {
        return trak;
    }

    public String getIcao() {
        return icao;
    }

    public String getOpicao() {
        return opicao;
    }

    String getCountry() {
        return country;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public String getAirlineName() {
        return  airlineName;
    }

    int getColour() {return colour; }

    public void setAircraftName(String acName) {
        this.aircraftName = acName;
    }

    public void setAirlineName(String airName) {
        this.airlineName = airName;
    }

    void setColour(int colour) {this.colour = colour; }

    void setUnitsNautical() {
        this.alt = alt + "feet";
        this.spd = spd + "knots";
        this.trak = trak + "\u00B0";
    }

    void setUnitsMetric() {
        try {
            this.metricAlt = Double.parseDouble(metricAlt) * 0.348 + "metres";
        } catch (NumberFormatException e) {
            //
        }
        try {
            this.metricSpd = Double.parseDouble(metricSpd) * 0.348 + "km/h";
        } catch (NumberFormatException e) {
            //
        }
    }

    //Ensure that airplane longitude is valid
    static boolean isValidLongitude(String l) {
        if (!l.equals("")) {
            float longitude = Float.parseFloat(l);
            return (!(180.0 < longitude)) && (!(longitude < -180.0));
        }
        else {
            return false;
        }
    }

    //Ensure that airplane latitude is valid
    static boolean isValidLatitude(String l) {
        if(!l.equals("")) {
            float latitude = Float.parseFloat(l);
            return (!(90 < latitude)) && (!(latitude < -90));
        }
        else {
            return false;
        }
    }

    // Check if plane is valid by using valid latitude and longitude methods, and also making sure plane has an ICAO code.
    boolean isValidPlane() {
        return isValidLatitude(this.lat) && isValidLongitude(this.lon) && this.icao != null && !this.icao.equals("");
    }
}
