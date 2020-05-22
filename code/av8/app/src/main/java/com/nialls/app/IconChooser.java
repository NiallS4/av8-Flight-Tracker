package com.nialls.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.nialls.R;
import com.nialls.main.MainActivity;

import java.util.ArrayList;

import static android.graphics.BitmapFactory.*;
import static android.graphics.Color.rgb;

// This class is used to easily change icons to different colours or shapes to enhance UI
public class IconChooser {

    // Shade plane icons a different colour
    // Takes RGB encoded colour and resource ID e.g. R.drawable.plane_icon_2
    private static BitmapDescriptor changeIconColour(int colour, int IconID) {
        Bitmap original = decodeResource(MainActivity.getInstance().getResources(), IconID);
        int icon_width = 80;
        int icon_height = 80;
        Bitmap aircraftIcon = Bitmap.createScaledBitmap(original, icon_width, icon_height, false);

        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(aircraftIcon);
        canvas.drawBitmap(aircraftIcon, 0, 0, paint);

        return BitmapDescriptorFactory.fromBitmap(aircraftIcon);
    }

    // Changes colour of aircraft to highlight to user
    // RGB for accent = (255, 64, 129)
    public static void changeToCurrentIcon (Marker marker) {
        BitmapDescriptor currentIcon = changeIconColour(rgb(255, 64, 129), R.drawable.plane_icon_2);
        marker.setIcon(currentIcon);
    }

    // Change icon colour back to default
    public static void changeToDefaultIcon (Marker marker) {
        Plane p = (Plane) marker.getTag();
        BitmapDescriptor defaultIcon = null;
        if (p != null) {
            defaultIcon = changeIconColour(p.getColour(), R.drawable.plane_icon_2);
        }

        marker.setIcon(defaultIcon);
    }

    static BitmapDescriptor initialAircraftIcon(Plane p) {
        return changeIconColour(p.getColour(), R.drawable.plane_icon_2);
    }

    // Take an altitude int and convert it to a colour int
    public static int altitudeToColour(int altitude) {
        // colours are in RGB format. Range from Yellow : 255 255 64, to Red : 255 64 64

        // If aircraft is at or below sea level, colour it an extra bright shade of yellow to highlight
        if (altitude <= 0){
            return rgb(255, 255, 0);
        }
        // If aircraft is flying extraordinarily high, paint it deep red
        else if (altitude > 45600) {
            return rgb(180, 0, 0);
        }
        // Else paint it somewhere in between
        else {
            int green = 255;
            // we will change green factor in RGB colour by increments of 5 as they are enough to be noticeable
            // change colour in increments of 1200 ft as 190 / 5 = 38, 38 * 1200 = 45600 (just above the max for most commercial aircraft)
            // e.g. if altitude is zero, green is unchanged. if altitude is 45900 then green is 64
            int adjust = ((altitude / 1200) * 5) + 65;
            green -= adjust;
            return rgb(255, green, 64);
        }

    }

    // Colour all icons depending on their altitude
    public static void colourIconsByAltitude (ArrayList <Marker> aircraftIcons) {
        int colour;
        for (Marker marker : aircraftIcons){
            Plane p = (Plane) marker.getTag();
            if (p != null && p.getIcao().equals(marker.getTitle()) && p.getAlt() != null) {
                int altitude;
                try {
                    altitude = Integer.parseInt(p.getAlt());
                } catch (NumberFormatException e) {
                    Log.e("Heatmap Error: ", String.valueOf(e));
                    altitude = 0;
                }
                colour = altitudeToColour(altitude);
                p.setColour(colour);
                marker.setIcon(changeIconColour(colour,  R.drawable.plane_icon_2));
            }
        }
    }

    // Take an speed int (ground speed) and convert it to a colour int
    public static int speedToColour(int speed) {
        // colours are in RGB format. Range from Pink : 255 128 255, to Blue : 128 128 255

        // If aircraft ground speed is 0, it is either stationary or flying at 90 degrees to the surface. either way paint it dark blue
        if (speed <= 0){
            return rgb(64, 64, 255);
        }
        // If aircraft is flying very fast (almost supersonic or beyond) paint it a darker shade to highlight
        else if (speed > 544) {
            return rgb(190, 0, 128);
        }
        // Else paint it somewhere in between
        else {
            int red = 128;
            // Use similar calculations as in altitudeToColourMethod

            int adjust = ((speed / 17) * 4);
            red += adjust;

            return rgb(red, 64, 255);
        }
    }

    // Colour all icons depending on their speed
    public static void colourIconsBySpeed (ArrayList <Marker> aircraftIcons) {
        int colour;
        for (Marker marker : aircraftIcons){
            Plane p = (Plane) marker.getTag();
            if (p != null && p.getIcao().equals(marker.getTitle()) && p.getAlt() != null) {
                int speed;
                try {
                    speed = (int) Double.parseDouble(p.getSpd());
                } catch (NumberFormatException e) {
                    Log.e("Heatmap Error: ", String.valueOf(e));
                    speed = 0;
                }
                colour = speedToColour(speed);
                p.setColour(colour);
                marker.setIcon(changeIconColour(colour, R.drawable.plane_icon_2));
            }
        }
    }
}