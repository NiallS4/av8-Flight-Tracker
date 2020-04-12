## 0. Table of Contents

1. [Overview](1--overview)  
2. [Installation Guide](#2--installation-guide)  
>2.1 [Requirements](#21-requirements)  
>2.2 [Permissions](#22-permissions)  
>2.3 [Installation Instructions](#23-installation-instructions)  
3. [User Guide](#3--user-guide)  
>3.1 [Flight Map](#31-flight-map)  
>3.2 [Refresh and Filtering](#32-refresh-and-filtering)  
>3.3 [Search for Aircraft by Registration](#33-search-for-aircraft-by-registration)  
>3.4 [Other Map Features](#34-other-map-features)  
>3.5 [Augmented Reality (AR)](#35-augmented-reality-ar)

***

## 1.  Overview

av8 is a flight tracking app for Android. The app displays all aircraft within 100 nautical miles (180 km) of your current location. It will also display aircraft anywhere in the world if the map is moved to that location. It displays information on each aircraft including airline, type, registration, altitude and speed.  
There are a number of extra features to enhance the map experience including heat maps for speed and altitude and filtering. It is possible to search for any aircraft currently in flight by registration.  
There is also an AR (Augmented Reality) component. This shows all aircraft within 25 nautical miles (nm) of your current location on your camera view, relative to the angle of your phone. av8 is primarily intended for aviation enthusiasts but we have taken measures to simplify various data so it can also be used by more casual users.

***

## 2.  Installation Guide

### 2.1 Requirements

The app can be installed on Android devices running at least Android 7.0 (“Nougat”). ARCore (Google Play Services for AR) compatibility is required for the AR component. A list of compatible devices is available here: [https://developers.google.com/ar/discover/supported-devices](https://developers.google.com/ar/discover/supported-devices) A small minority of devices require a minimum of Android 8.0 or 9.0 to use AR (see above link for details). If your device does not support ARCore, all other app features will still work. An internet connection is required at all times.

### 2.2 Permissions

The AR feature requires both location and camera access. It is advised but not essential to grant location access for the map.

### 2.3 Installation Instructions

To install the app, first ensure that installing apps from unknown sources is enabled. It should be possible to find this option in the device settings by searching for it. Otherwise a web search with your device and/or Android OS version should tell you where to find this setting.  
Following this, just download the file *av8.apk* and tap on the downloaded file and select “install” when prompted.

***

## 3.  User Guide

### 3.1 Flight Map

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/map.png?raw=true "Flight Map")


After opening the app, the flight map will be displayed. It shows all aircraft within 100 nm of your current location, or if location permissions are not granted, Dublin by default. Each aircraft is represented by a black plane icon. Each icon is rotated based on the plane’s heading. To update current location, use the location button at the top right hand corner of the map. Your current location is shown as a blue dot on the map.

Small Infobox              |  Expanded Infobox
:-------------------------:|:-------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/small_infobox.png?raw=true "Small Infobox")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/expanded_infobox.png?raw=true "Expanded Infobox") 


To view aircraft details, tap on a plane marker. A small information box will be displayed at the bottom left hand corner of the screen. The selected aircraft will turn pink. The small infobox displays airline, callsign, registration and type (can vary depending on available information). Tap on the information box to expand it. The expanded box typically shows all the previously mentioned information, plus altitude, speed, heading and country of registration. Tap the box again to shrink it. To close the information box, tap the selected aircraft icon.

### 3.2 Refresh and Filtering

Refresh Button             |  Filter Options
:-------------------------:|:-------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/refresh.png?raw=true "Refresh Button")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/filter_options.png?raw=true "Filter Options")


To refresh the flight map, use the refresh button on the action bar (highlighted). This will show all aircraft in the current map view.

It is possible to filter the planes in the current view. To open the filter menu, tap the ellipsis button (three vertical dots) and then filter. A menu displays which has three options, filter by type, operator and country.

Filter by Operator - Aeroflot  |  Only Aeroflot Aircraft Shown
:-----------------------------:|:-----------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/filter_op1.png?raw=true "Filter by Operator - Aeroflot")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/filter_op2.png?raw=true "Only Aeroflot Aircraft Shown")


For example, to filter by operator, enter the airline’s ICAO code. In the images above, we show how to only show Aeroflot planes around Moscow. The ICAO code for Aeroflot “AFL” is entered. Only Aeroflot planes are shown. The process is the same for aircraft types; e.g. enter “A320” to only show Airbus A320 aircraft. ICAO codes can be found easily on the likes of Wikipedia if you do not know the code for the airline or aircraft you wish to filter by. To filter by country, enter the country’s full name, e.g. “Ireland” or “United Kingdom”. To clear the filter, use the refresh button.

### 3.3 Search for Aircraft by Registration

Search Button                  |  Location of Searched Plane
:-----------------------------:|:-----------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/search1.png?raw=true "Search Button") |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/search2.png?raw=true "Location of Searched Plane")


To search for any aircraft currently in flight (worldwide) by registration, tap the highlighted search button in the actionbar. Enter a registration, e.g. “D-AINC” and tap the search button. If the plane is tracking, its location will be shown.

### 3.4 Other Map Features

Options Menu               |  Military Aircraft
:-------------------------:|:-------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/options.png?raw=true "Options Menu")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/military.png?raw=true "Military Aircraft")


By tapping on the ellipsis (three vertical dots) in the actionbar a number of additional options are shown. To display all military aircraft in the world, select “Show all military aircraft”. They are displayed in an “army green” colour. To reset the map, use the refresh button. There is a settings sub-menu where the map type can be changed. There is a setting to change the AR view distance to 100 nautical miles instead of 25. It is also possible to show a compass on the map view. The “Help” option shows basic instructions on how to use the app.

Altitude Heat Map          |  Speed Heat Map
:-------------------------:|:-------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/heat_alt.png?raw=true "Altitude Heat Map")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/heat_spd.png?raw=true "Speed Heat Map")

There are also two heat maps options in the menu. The altitude heat map colours the aircraft icons based on their altitude. It goes from yellow to red (low to high). Pure yellow indicates that the plane is at ground level, while deep red (> 45,000 feet) may indicate a drone or fighter jet as commercial aircraft cannot fly this high.

There is also a speed heat map. It goes from blue to red (slow to fast). Stationary aircraft or hovering helicopters are blue while very fast aircraft (like fighter jets) are coloured red. Again, to reset the map use the refresh button.

### 3.5 Augmented Reality (AR)

AR Aircraft Markers            |  AR Marker Full Information
:-----------------------------:|:-----------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/ar1.png?raw=true "AR Aircraft Markers")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/ar2.png?raw=true "AR Marker Full Information")

To launch the AR feature, tap on “AR” in the actionbar. You must grant access to location and camera permissions to use the AR. Point your phone up at the horizon. After a few seconds, markers will be rendered which show the position of planes in the area relative to your location and device camera. Tap on a marker to show more information. The markers refresh every five seconds. The markers are scaled based on distance. The distance between the plane and the user is shown in metres/kilometres. To exit the AR, tap the back button. The AR view distance can be increased from the default 25 nm to 100 nm if desired (not recommended in areas of high air traffic).
