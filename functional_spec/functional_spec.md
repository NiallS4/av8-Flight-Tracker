## 0. Table of Contents

1. [Introduction](#1--introduction)  
>1.1 [Overview](#11-overview)  
>1.2 [Business Context](#12-business-context)  
>1.3 [Glossary](#13-glossary)  
2. [General Description](#2--general-description)  
>2.1 [Product / System Functions](#21-product--system-functions)  
>2.2 [User Characteristics and Objectives](#22-user-characteristics-and-objectives)  
>2.3 [Operational Scenarios](#23-operational-scenarios)  
>2.4 [Constraints](#24-constraints)  
3. [Functional Requirements](#3--functional-requirements)  
>3.1 [Web Server and Hosted Application](#31-web-server-with-hosted-application)  
>3.2 [Interactive Map](#32-interactive-map)  
>3.3 [API call](#33-api-call)  
>3.4 [Aircraft Icons on Map](#34-aircraft-icons-on-map)  
>3.5 [View Aircraft Details](#35-view-aircraft-details)  
>3.6 [AR Vision](#36-ar-vision)  
>3.7 [Statistics Database](#37-statistics-database)  
>3.8 [Flight Statistics](#38-flight-statistics)  
>3.9 [User Preferences](#39-user-preferences)  
>3.10 [Refresh Function](#310-refresh-function)  
4. [System Architecture](#4--system-architecture)  
5. [High Level Design](#5--highlLevel-design)  
>5.1 [Context Diagram](#51-context-diagram)  
>5.2 [Data Flow Diagram](#52-data-flow-diagram)  
6. [Preliminary Schedule](#6--preliminary-schedule)  
7. [Appendices](#7--appendices)

***

## 1. Introduction

### 1.1 Overview

The application to be developed is a flight tracking application for Android devices. Its primary feature will be a map which will display flight traffic around the user’s location or a location they navigate to. The aircraft will be represented on the map by plane icons. It will be possible to search for active aircraft by callsign, registration, airline etc. The app will include a filtering system where only aircraft that fulfil one or more selected criteria are displayed, e.g. airline, aircraft type, origin or destination.

There will also be an AR (augmented reality) feature whereby the user can point their phone at the horizon and the nearest aircraft in the vicinity are visible through their phone camera. Selecting the plane would show the full information for that aircraft, as in the standard map view.

We are also planning to implement a statistics section where users can view data such as delay statistics, on-time performance, average flight times and traffic and route figures. This information will be displayed interactively and graphically within the app.
We will also collect information on user usage patterns in order to make improvements to the app.

This app is being developed primarily to serve the needs of aviation enthusiasts. We aim to provide as much detail and technical information as possible to best serve our target audience. The app will also be designed in a way to make it as user friendly as possible, so that casual users will also be encouraged to use the app. For instance, if someone wants to know what plane is flying over their house.


### 1.2 Business Context

Not applicable. We intend to release this app as freeware software. It will not be affiliated with any business.


### 1.3 Glossary
**GPS**
**G**lobal **P**ositioning **S**ystem is a system which uses satellites to track the geolocation of any object that is carrying a GPS receiver and is within view of GPS satellites.

**ADS–B**
**A**utomatic **D**ependent **S**urveillance–**B**roadcast is a technology where an aircraft broadcasts is current location via GPS.

**API**
An **A**pplication **P**roduct **I**nterface is a set of protocols and tools for developing software applications. APIs will be central to retrieve the ADS–B flight data for our app.

**JSON**
**J**ava**S**cript **O**bject **N**otation is a text syntax used when receiving data from a  server to a user. The API information is in JSON format.

**AR**
**A**ugmented **R**eality is a technology that provides a CGI overlay on a user’s view (the phone camera). This is not to be confused with Virtual Reality (VR) which provides a complete digital recreation of a real-world setting.

**ICAO codes**
**I**nternational **C**ivil **A**viation **O**rganization codes are four letter airport codes (e.g. EIDW), four letter aircraft codes (e.g. A346) and three letter airline codes (e.g. EIN). The API data is displayed as these codes mostly.

**IATA codes**
**I**nternational **A**ir **T**ransport **A**ssociation codes are more user familiar type of codes that will be displayed in the app. They are two letters for airlines (e.g. EI) and three letters for airports (e.g. DUB).

**NM**
**N**autical **M**ile, the standard measurement of distance set out by ICAO to be used both air and marine navigation.

***

## 2. General Description

### 2.1 Product / System Functions
The system will include the following functions:
* Mobile application as the main user interface.
* Web application for processing client requests (backend).
* Map with an overlay displaying aircraft locations.
* Filter map by airline, aircraft type, destination and distance from user.
* Refresh button to refresh the map. This will also happen automatically after a certain period of time as elapsed.
* AR module which allows users to use their device's camera to identify aircraft within a certain radius.
* Statistics component that will allow users to view desired flight statistics.
* Preferences section where users can set which map type they want to use (e.g. satellite, hybrid or road), set how each aircraft icon is labelled, colours and other preferences.

The above system functions will be described in detail in section 3.

### 2.2 User Characteristics and Objectives
The application will be accessible to anyone with an Android device. As our primary target market is aviation enthusiasts, most of our intended functionality is focused around the features that this audience is likely to want. The app will also be user-friendly to also make it appealing to the more typical Android user.

From the user’s perspective, they will require a fully functioning app that retrieves the flight data quickly. The map should be easy to navigate and aircraft icons should be displayed clearly. The aircraft should be clearly labelled so planes of interest can be found quickly. These labels may include airline, registration and callsign should be customisable by the user. The technical data should be displayed from most to least important. Vital information such as airline, aircraft type and destination should be at the top. Less important information such as heading, altitude and transponder codes should be located closer to the bottom of the list of information. Other possible desirable information may include a photo of the aircraft in question, historical flight information and scheduled times of arrival and departure. The design of the AR should be minimalist to make it as usable as possible.

Most desirable feature from the user’s perspective are implementable. However there are some features that may have to be omitted for cost, time or accessibility reasons. Displaying images of each exact aircraft will likely not be possible for copyright reasons. A possible compromise could be to have one public domain or attribution licenced photo for each model of aircraft (irrespective of airline). It also may not be possible to include some aforementioned information such as scheduled flight times due to the limitations of the APIs.

### 2.3 Operational Scenarios

*2.3.1 User Opens Application*
The user will not be required to provide details or log in to access the application. After they have installed the application on their device all features will be available to them. The user will be presented with the home screen which is going to be the flight map displaying aircraft within a user specified range.
The device will send a request to the web server which will then call the API. This request will include the user's current location or else the co-ordinates of the location they have selected. The API will return the results (list of aircraft) in JSON format. The server will interpret this information and send it to the mobile device. The results will be displayed on the map or AR as plane icons.

*2.3.2 User Selects An Aircraft*
If a user interacts with the icon of a specific aircraft by tapping on the screen a popup will appear showing information on that flight. Information will include ICAO code, IATA code, aircraft altitude & velocity, and GPS co-ordinates. This information is supplied by the API.

*2.3.3 User Filters Flight Map*
The user wants to only look at a subset of planes based on a certain criteria, so they tap on the filter icon and are given a choice of what they want to filter by. The user may filter by one or more categories to produce the desired subset of aircraft. This does not send another API call but rather only shows the current results that meet the selected criteria.

*2.3.4 User selects refresh button*
If the user taps the refresh button, the same process outlined in 2.3.1 will be repeated. The map/AR will also be refreshed automatically after a certain timeframe (e.g. 30 seconds).

*2.3.5 User Opens AR vision*
After the user taps on the icon for AR vision, the application will immediately display the feed provided by the devices main camera. If there are any aircraft within a specified range of the user, an icon will be shown as an overlay on the camera feed where the application detects the aircraft to be in flight. As with the map, the user may then tap on the icon to provide more information on the aircraft.
Like with the map, the displayed aircraft will only refresh if the user presses the refresh button or the automatic refresh period has passed.

*2.3.6 User Opens Flight Statistics*
The flight statistics screen will display various statistics to the user. These will include delays by airport, delays by airline and delays by weekday. The statistics can be presented to the user in both text and graph form.
The mobile client sends a request to the web server for the latest version of the relevant statistics. The server retrieves this information from the SQL database and returns it to the client.

*2.3.7 User Opens App Preferences*
The user preferences/settings menu will display a list of customisable options within the app. These shall include the map type, units (imperial or metric) and time formats.

### 2.4 Constraints

**Time**
As there is only a short period of time between when the app is likely to be up and running and the date for the submission of the project, the data analytics part of the project has the risk of being limited in nature. There may only be user data information for a short period. The analysis of flight data will have to be started well before the app is fully functional in order to provide meaningful data.

**Cost**
While the majority of tools we intend to use are freeware, there may be ideal tools available which must be paid for. The availability of capital may restrict us from using the best software. We will have to use free software for the most part and reasonably priced products if necessary. For example there are plenty of useful APIs for flight statistics but these can cost around $100 a month. Many also do not have prices listed as they are intended for corporate use.

**Limitations of the APIs**
As we are reliant on APIs in order to collect the ADS-B data for the flights, there are some desirable features that may not be possible to implement. If certain data required for a feature is not available from an API, there is very little we can do to resolve this. Some APIs also have request limits and not all are free. Sometimes certain data, particularly origin and destination information can be incorrect. Again these anomalies are outside our control.

***

## 3 Functional Requirements

### 3.1 Web Server with Hosted Application

* *Description*
The web application will be for processing client requests only, it will not be accessible to users.
As most of the functions of the system will require the client to communicate with a server, there must be an application hosted on a server that processes client requests and delivers data back which will then be utilised by client-side code.

* *Criticality*
The web app is essential for most of the system to operate. Otherwise the client will not be getting any data to display.

* *Technical issues*
A suitable server host must be chosen so that there is minimal down time.

* *Dependencies*
None

### 3.2 Interactive Map

* *Description*
The core function of the system is to provide an interactive map. When the user opens the app the map is shown immediately, taking up most of the device screen. The map will show aircraft within a certain radius of the location the user is viewing. This radius is relative to the zoom level of the map. The default is 100 nm (180 km). All aircraft within this radius will be displayed. The map is planned to be an OpenStreetMap map.
* *Criticality*
This function is absolutely essential to our project. The map is the foundation of our app. If the map does not function correctly, most other aspects of the app will also not work, the AR in particular. For this reason we will prioritise getting the map working before anything else.
* *Technical issues*
The primary potential for issues with the map relates to the movement and navigation of the map function. It should be possible to navigate to any location in the world (with the default being the user's current location). It should also be possible to zoom and rotate the current view. Rotating the current view could possibly cause issues with the planes not being displayed correctly.
* *Dependencies*
None, this is part of the core client application.

### 3.3 API Call

* *Description*
It is necessary to call an API (ADS-B Exchange) in order to retrieve the data needed for the application. When the user opens the app, their GPS co-ordinates will be sent to the web server which will then call the API. The API will return a list of results in JSON format. Each result indicates an individual plane. It contains information such as location (latitude and longitude), callsign, aircraft type, airline, speed (knots), altitude (feet), registration, origin and destination airports.

* *Criticality*
This function is crucial to the application. The aircraft positions cannot be displayed on either the map or within the AR section without the API call function working fully.


* *Technical issues*
In the unlikely event of API downtime, this will prevent almost all aspects of the app from functioning. It may be advisable to have a backup API to provide for such an eventuality. Other APIs may not have as extensive information however.


* *Dependencies*
Web server with hosted application (3.1)

### 3.4 Aircraft Icons on Map

* *Description*
Once the map has been loaded, the aircraft icons will then be displayed on the map. This will be achieved by calling the API with the GPS co-ordinates of the user. All planes within a 100 nm radius (by default) will be shown on the map. The aircraft will be represented by plane shaped icons.
The locations of the aircraft will be obtained by calling the API.

* *Criticality*
As with the map, this requirement is vital to the success and functioning of the system. If the planes are not displayed correctly, then other functions such as the AR will also not work. It is essential that the planes are clearly visible and displayed in the correct locations in order to provide a user friendly experience.

* *Technical issues*
This requirement should be relatively straightforward to implement. It is however entirely reliant on the information provided from the API so any inaccuracies in its information cannot be avoided.

* *Dependencies*
Displaying the plane icons is fully reliant on all the previous requirements.

### 3.5 View Aircraft Details

* *Description*
By selecting on aircraft on the map or in the AR camera, the most important details on the plane in question will be displayed. These details will be airline, flight number, aircraft, and registration. By selecting the more details button, the full information from the API will be displayed (as mentioned in section 3.3).

* *Criticality*
This is very important. Without this function, the aircraft icons shown will have no context. Plane icons on a map without accompanying information are fairly meaningless and not very interesting for the user.

* *Technical issues*
Again, the accuracy of information is reliant on the API, which is in turn dependent on the information obtained from ADS-B receivers.

* *Dependencies*
Dependent on the web server with hosted application (3.1) and API call (3.3) functions.

### 3.6 AR Vision
* *Description*
The AR vision function will help users identify aircraft within their line of sight using their device's camera. If the aircraft that the user wants to identify is having its information transmitted by ADS-B, a plane icon will overlay on the camera feed where the application predicts the aircraft to be relative to the user's position.

* *Criticality*
The AR function is non-essential but highly desirable as it provides a convenient and sophisticated method for users to identify nearby aircraft.

* *Technical issues*
We anticipate the AR vision to be the most technically demanding function in the application. A solution will have to be devised for finding the user's orientation and using that data to calculate where the icons should appear on the camera feed. 

* *Dependencies*
Dependent on web server with hosted application (3.1), interactive map (3.2), API call (3.3), aircraft icons on map (3.4) and view aircraft details (3.5) functions.

### 3.7 Statistics Database
* *Description*
We intend to include a database of various flight related statistics. Although most of this information is already available, it is mostly presented in a very technical manner in APIs or Excel/CSV files. The average user will not be able or willing to navigate through such vast amounts of data in order to find what they are looking for.
We plan to include traffic and route statistics for Irish airports, flight time data and airline/airport on-time performance figures if viable. In particular, we will analyse delay statistics by day of the week and by month.

* *Criticality*
This function is not absolutely essential however it is necessary for the statistics section of the app (see below). The app will still succeed in its base function without this requirement but we feel it makes the app more interesting and provides valuable information to the user base in a user friendly layout.

* *Technical issues*
This function will require setting up a SQL database. The web app must be able to access the database.

* *Dependencies*
Web server with hosted application (3.1)

### 3.8 Flight Statistics
* *Description*
The flight statistics function will allow users to view useful statistics as outlined in section 3.7. These will be presented in an interactive, graphical manner to make them visually appealing to the user. Due to time constraints, the data gathered for the statistics will come from an already established external source. This data will have to be stored in a database that will communicate with the web application. The data will be presented in an interactive manner, so users will be able to view data by day, week, month and quarter.

* *Criticality*
This function is desirable because it adds another utility to the application which users will find helpful.

* *Technical issues*
None predicted. The only issue that could arise here is bad data, but we will be checking for accuracy in any data we use.

* *Dependencies*
Dependent on web server with hosted application (3.1) and statistics database (3.7).

### 3.9 User Preferences
* *Description*
There will be a settings option in the application menu where users can choose from a variety of different settings. For example they will be able to choose:  
    - What type of map they wish to use (standard, satellite, hybrid).
    - The size of the plane icons.
    - How the icons are labelled (i.e. by callsign, registration, airline).
    - Units (imperial and metric).
    - Time (UTC, user local time, airport local time).
The default settings will be imperial/nautical for units (ICAO standards) and airport local for times. The default type of map has yet to be determined.

* *Criticality*
This is not necessary however it enhances the user experience of the app. These options could just be fixed, but allowing the user to customise the app to their own preferences makes it a much better experience for the user.

* *Technical issues*
None expected. It should be straightforward to convert units and time formats.

* *Dependencies*
Dependent on interactive map (3.2), API call (3.3) and aircraft icons on map (3.4).

### 3.10 Refresh Function
* *Description*
The refresh button will be located on the home page of the app. Its function will be to update the map or the AR view. It will use the API call function outlined in section 3.3. The map/AR will also automatically refresh after a set time period, for example 30 seconds.

* *Criticality*
The refresh function is vital as otherwise the user would have to close and reopen the app to refresh the map. The manual refresh button is not an essential feature as long as the map will auto-refresh. However it is a simple addition and will improve the user experience.

* *Technical issues*
None expected.


* *Dependencies*
Dependent on web server with hosted application (3.1) and API call (3.3).

***

## 4 System Architecture
This is a high level diagram that represents the architecture of our system.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/functional_spec/images/system_architecture.png?raw=true "System Architecture Diagram")

* The user accesses the application on their mobile Android device.
* The mobile client takes the user's current location and sends these co-ordinates to the web server.
* The web server sends the co-ordinates to the API along with the distance parameter (default 100 nm).
* The API returns a list of results (the aircraft) in JSON format. This information is interpreted by the web server which sends it to the mobile client.
* The mobile client takes each aircraft instance and displays its position of the map based on its co-ordinates. Selecting a plane displays the full information from the API.
* If the user taps the "refresh" button or the app auto refreshes after a certain period of time, the same process outlined above will repeat, updating the map.
* The AR function uses the same API call process as the map. The fundamental information contained within in it is the same as in the map.
* The SQL database of statistics is built from various APIs and published Excel or CSV files from agencies such as the CSO or EUROCONTROL.
* If the user wishes to view these statistics they are retrieved from the database by the server and sent to the mobile client. This information is displayed graphically if required by the mobile application.

***

## 5. High Level Design

### 5.1 Context Diagram
This diagram shows how the av8 web server interacts with the relevant entities (users, admins, the API and the database).

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/functional_spec/images/context.png?raw=true "Context Diagram") 

### 5.2 Data Flow Diagram
This diagram shows the flow of data between the various entities, both internal and external.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/functional_spec/images/dfd.png?raw=true "Data Flow Diagram")

***

## 6. Preliminary Schedule
Here we have set a preliminary schedule in the form of a Gantt chart.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/functional_spec/images/preliminary_schedule.png?raw=true "Preliminary Schedule Gantt Chart")

***

## 7. Appendices

### Resources
* [ADS-B Exchange](https://www.adsbexchange.com/)
* [The Open Sky Network](https://opensky-network.org/)
* [FlightRadar24](https://www.flightradar24.com/)
* [OpenStreetMap](https://www.openstreetmap.org/)
* [Android Developers](https://developer.android.com/)
* [Firebase](https://firebase.google.com/)
