# av8 - Flight Tracking App

A flight tracking app for Android. This was primarily developed from January to March 2020 by myself and Karl Finnerty for our DCU Computer Applications 3rd Year Project.

### App features

* Map which displays all aircraft within 180 km of the user's current location or a location of their choosing. Details on each plane are shown in an expandable infobox.
* Augmented Reality (AR) feature which renders all nearby aircraft as markers on the user's phone camera, relative to the angle and orientation of the phone.
* Filtering system to only show aircraft based on airline, aircraft type or country of registration.
* Heat maps which colour aircraft based on altitude or speed.
* Search aircraft by registration.
* View all military aircraft positions in the world.

Detailed explanations of the app's features are included in the user manual.

av8 Map                    |  av8 AR
:-------------------------:|:-------------------------:
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/expanded_infobox.png?raw=true "av8 Map with Expanded Inbfobox")  |  ![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/user_manual/images/ar1.png?raw=true "av8 AR Feature")

### Requirements

* Android device running at least Android 7.0 (“Nougat”).
* Google Play Services for AR (ARCore) must be installed to use the AR component. [List of compatible devices](https://developers.google.com/ar/discover/supported-devices).

### Installation Guide

1. Before installing the app, first ensure that installing apps from unknown sources is enabled. It should be possible to find this option in the device settings by searching for it. Otherwise a web search with your device and/or Android OS version should tell you where to find this setting.

2. Select the APK file on your Android device and choose “install” when prompted.

It is necessary to grant location and camera access to make use of the app’s full functionality (including the AR).
