package com.nialls.app;

public class SnippetFormatter {

    // Formats snippet depending on information available from API & database
    public static String format(Plane p) {
        String snippet;
        if(p.getAircraftName().equals("") && p.getAirlineName().equals("")) {
            snippet = "Callsign: " + p.getCall() + "\nReg: " + p.getReg() +
                    "\nType: " + p.getType() +
                    "\nAltitude: " + p.getAlt() + " feet" +
                    "\nSpeed: " + p.getSpd() + " knots" +
                    "\nHeading: " + p.getTrak() + "\u00B0" +
                    "\nCountry: " + p.getCountry();
        }
        else if (p.getAircraftName().equals("")) {
            snippet = "Airline: " + p.getAirlineName() +
                    "\nCallsign: " + p.getCall() + "\nReg: " + p.getReg() +
                    "\nType: " + p.getType() +
                    "\nAltitude: " + p.getAlt() + " feet" +
                    "\nSpeed: " + p.getSpd() + " knots" +
                    "\nHeading: " + p.getTrak() + "\u00B0" +
                    "\nCountry: " + p.getCountry();
        }
        else if (p.getAirlineName().equals("")) {
            snippet = "Callsign: " + p.getCall() + "\nReg: " + p.getReg() +
                    "\nType: " + p.getAircraftName() + " (" + p.getType() + ")" +
                    "\nAltitude: " + p.getAlt() + " feet" +
                    "\nSpeed: " + p.getSpd() + " knots" +
                    "\nHeading: " + p.getTrak() + "\u00B0" +
                    "\nCountry: " + p.getCountry();
        }
        else {
            snippet = "Airline: " + p.getAirlineName() +
                    "\nCallsign: " + p.getCall() + "\nReg: " + p.getReg() +
                    "\nType: " + p.getAircraftName() + " (" + p.getType() + ")" +
                    "\nAltitude: " + p.getAlt() + " feet" +
                    "\nSpeed: " + p.getSpd() + " knots" +
                    "\nHeading: " + p.getTrak() + "\u00B0" +
                    "\nCountry: " + p.getCountry();
        }
        return snippet;
    }
}
