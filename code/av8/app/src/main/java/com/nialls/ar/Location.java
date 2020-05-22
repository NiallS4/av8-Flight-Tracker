package com.nialls.ar;

public final class Location {
    private String latitude;
    private String longitude;

    static Location NULL_LOCATION = new Location(null, null);

    String getLatitude() {
        return latitude;
    }

    String getLongitude() {
        return longitude;
    }

    Location(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}