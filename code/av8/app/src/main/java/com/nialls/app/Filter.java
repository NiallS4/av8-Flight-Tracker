package com.nialls.app;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nialls.main.MainActivity;

public class Filter {

    private Context mContext;

    public Filter(Context context) {
        mContext = context;
    }

    public void filterByType() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        final EditText edittext = new EditText(mContext);
        alert.setMessage("Enter aircraft type ICAO code below");
        alert.setTitle("Filter by Aircraft");
        edittext.setGravity(Gravity.CENTER_HORIZONTAL);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        edittext.setHint("Example: A320, B738, C130, BCS3");
        alert.setView(edittext);

        alert.setPositiveButton("Filter", (dialog, whichButton) -> {
            String type = edittext.getText().toString().toUpperCase();

            APICaller.aircraftIcons.clear();
            MainActivity.getInstance().getmMap().clear();

            for(Plane p : APICaller.getAircraft()) {
                String snippet = SnippetFormatter.format(p);
                // Check if co-ordinates are valid before plotting on the map
                if(p.isValidPlane() && p.getType().equals(type)) {
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon())))
                            .title(p.getIcao())
                            .icon(IconChooser.initialAircraftIcon(p))
                            .snippet(snippet);

                    // Rotates marker based on aircraft heading (bearing)
                    if (!p.getTrak().equals("")) {
                        marker.rotation(Float.parseFloat(p.getTrak()));
                    }
                    else {
                        marker.rotation((float) 0.0);
                    }
                    // Adds marker to map
                    APICaller.aircraftIcons.add(MainActivity.getInstance().getmMap().addMarker(marker));
                    APICaller.aircraftIcons.get(APICaller.aircraftIcons.size() - 1).setTag(p);
                }
                else {
                    Log.e("Invalid_Plane", p.toString());
                }
            }
        });
        alert.show();
    }

    public void filterByOperator() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        final EditText edittext = new EditText(mContext);
        alert.setMessage("Enter airline ICAO code below");
        alert.setTitle("Filter by Operator");
        edittext.setGravity(Gravity.CENTER_HORIZONTAL);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        edittext.setHint("Example: EIN, RYR, BAW, DLH");
        alert.setView(edittext);

        alert.setPositiveButton("Filter", (dialog, whichButton) -> {
            String operator = edittext.getText().toString().toUpperCase();

            APICaller.aircraftIcons.clear();
            MainActivity.getInstance().getmMap().clear();

            for(Plane p : APICaller.getAircraft()) {
                String snippet = SnippetFormatter.format(p);
                // Check if co-ordinates are valid before plotting on the map
                try {
                    if (p.isValidPlane() && (p.getOpicao().equals(operator) || p.getCall().substring(0, 3).equals(operator))) {
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon())))
                                .title(p.getIcao())
                                .icon(IconChooser.initialAircraftIcon(p))
                                .snippet(snippet);

                        // Rotates marker based on aircraft heading (bearing)
                        if (!p.getTrak().equals("")) {
                            marker.rotation(Float.parseFloat(p.getTrak()));
                        } else {
                            marker.rotation((float) 0.0);
                        }
                        // Adds marker to map
                        APICaller.aircraftIcons.add(MainActivity.getInstance().getmMap().addMarker(marker));
                        APICaller.aircraftIcons.get(APICaller.aircraftIcons.size() - 1).setTag(p);
                    } else {
                        Log.e("Invalid_Plane", p.toString());
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    Log.e("Error:", String.valueOf(e));
                }
            }
        });
        alert.show();
    }

    public void filterByCountry() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        final EditText edittext = new EditText(mContext);
        alert.setMessage("Enter country below");
        alert.setTitle("Filter by Country");
        edittext.setGravity(Gravity.CENTER_HORIZONTAL);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edittext.setHint("Example: Ireland, United Kingdom");
        alert.setView(edittext);

        alert.setPositiveButton("Filter", (dialog, whichButton) -> {
            String country = edittext.getText().toString();

            APICaller.aircraftIcons.clear();
            MainActivity.getInstance().getmMap().clear();

            for(Plane p : APICaller.getAircraft()) {
                String snippet = SnippetFormatter.format(p);
                // Check if co-ordinates are valid before plotting on the map
                if(p.isValidPlane() && p.getCountry().equals(country)) {
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon())))
                            .title(p.getIcao())
                            .icon(IconChooser.initialAircraftIcon(p))
                            .snippet(snippet);

                    // Rotates marker based on aircraft heading (bearing)
                    if (!p.getTrak().equals("")) {
                        marker.rotation(Float.parseFloat(p.getTrak()));
                    }
                    else {
                        marker.rotation((float) 0.0);
                    }
                    // Adds marker to map
                    APICaller.aircraftIcons.add(MainActivity.getInstance().getmMap().addMarker(marker));
                    APICaller.aircraftIcons.get(APICaller.aircraftIcons.size() - 1).setTag(p);
                }
                else {
                    Log.e("Invalid_Plane", p.toString());
                }
            }
        });
        alert.show();
    }

}
