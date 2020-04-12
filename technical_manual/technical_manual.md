## 0. Table of contents

1. [Introduction](#1--introduction)  
>1.1 [Overview](#11-overview)  
>1.2 [Glossary](#12-glossary)  
2. [System Architecture](#2--system-architecture)  
>2.1 [Overall Architecture](#21-overall-architecture)  
>2.2 [System Design Decisions](#22-system-design-decisions)  
3. [High-Level Design](#3--high-level-design)    
4. [Testing And Validation](#4--testing-and-validation)  
>4.1 [User Tests](#41-user-tests)  
>4.2 [Integration Tests](#42-integration-tests)  
>4.3 [Instrumentation Tests](#43-instrumentation-tests)  
>4.4 [Unit Tests](#44-unit-tests)  
>4.5 [Regression Tests](#45-regression-tests)  
5. [Problems and Resolution](#5--problems-and-resolution)  
> [Extra charges for API usage exceeding the quota](#extra-charges-for-api-usage-exceeding-the-quota)  
> [Setting up the cloud functions on Firebase](#setting-up-the-cloud-functions-on-firebase)  
> [The default Google Maps infobox only allowing a small number of characters](#the-default-google-maps-infobox-only-allowing-a-small-number-of-characters)  
> [Representing a set of coordinates as a marker within the AR scene](#representing-a-set-of-coordinates-as-a-marker-within-the-ar-scene)  
> [Overlapping marker in the AR scene](#overlapping-marker-in-the-ar-scene)  
> [Oversensitivity in device sensors](#oversensitivity-in-device-sensors)  
6. [Differences From Functional Specification](#6--differences-from-functional-specification)  
7. [Installation Guide](#7--installation-guide) 

***

## 1. Introduction

### 1.1 Overview

The system developed “av8”, is a mobile application for Android OS. It allows users to view and track aircraft anywhere in the world.
Its primary feature is a map with active aircraft displayed as icons which the user may interact with by tapping on the screen. The aircraft are tracked in real-time by using a network of ADS-B receivers, and the map is updated when the user taps the refresh button. When an icon is tapped, the aircraft is highlighted and an info box appears. The info includes the aircraft ICAO code, registration, and airline. The user may expand the info box to show more details such as the altitude, speed, aircraft type and heading.

A major feature of the application, and one that sets the app apart from the majority of flight trackers out there is the AR component. This allows users to dynamically identify nearby aircraft with the help of their device’s camera. It works by using the same data used to plot the aircraft on the flight map, but also takes the user's location and device’s sensors into account to display the aircraft positions relevant to the user.

The app has some more advanced tools such searching for a plane by its registration, filtering the shown aircraft by a certain criteria and displaying heat maps of aircraft altitude or speed. These features give users more control of what they want to see and show interesting patterns that may not otherwise be apparent.

### 1.2 Glossary

**Firebase**  
A mobile and web application development platform owned by Google.

**Android Studio**  
A tool for managing and developing Android applications owned by Google.

**GPS**  
**G**lobal **P**ositioning **S**ystem is a system which uses satellites to track the location of any object that is carrying a GPS receiver and is within view of GPS satellites.

**ADS–B**  
**A**utomatic **D**ependent **S**urveillance–**B**roadcast is a technology where an aircraft broadcasts is current location via GPS.

**API**  
An **A**pplication **P**roduct **I**nterface is a set of protocols and tools for developing software applications. APIs will be central to retrieve the ADS–B flight data for our app.

**JSON**  
**J**ava**S**cript **O**bject **N**otation is a text syntax used when receiving data from a server to a user. The API information is in JSON format.

**AR**  
**A**ugmented **R**eality is a technology that provides a computer-generated image overlay on a user’s view (the phone camera). This is not to be confused with Virtual Reality (VR) which provides a complete digital recreation of a real-world setting.

**ICAO codes**  
**I**nternational **C**ivil **A**viation **O**rganization codes are four letter airport codes (e.g. EIDW), four letter aircraft codes (e.g. A346) and three letter airline codes (e.g. EIN). The API data is displayed as these codes.

**NM**  
**N**autical **M**ile, the standard measurement of distance set out by ICAO to be used both air and marine navigation.

***

## 2. System Architecture

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/technical_manual/images/arch.png?raw=true "System Architecture Diagram")

### 2.1 - Overall Architecture

The system consists of four components. The first is the client, the mobile Android application. The user interacts with the system through the mobile app. The second is the web server. This was developed through Firebase. The database is a Firebase Firestore database. The final component is the ADS-B Exchange API.

The Firebase database is a Firestore real time database. It consists of two extensive tables. The first one is a mapping of aircraft ICAO codes to full aircraft names. For example, “A320” : “Airbus A320” and “B748” : “Boeing 747-8I”. The second table is a mapping of airline ICAO codes to full airline names. For example, “EIN” : “Aer Lingus” and “THY” : “Turkish Airlines”. This includes the vast majority of European airlines and most airlines in the rest of the world.  
This database is very useful to the system as the codes included in the API data are not particularly user friendly. The mobile client only reads from the database, the administrators add new entries to the database.

The primary component of the Firebase server is the cloud functions. These functions call the ADS-B Exchange API and return this information to the client application. The cloud functions consist of JavaScript functions which:  
* Return all aircraft within x nm of a given coordinate. It takes three parameters: latitude, longitude and distance.   
* Return details on an aircraft based on its registration. It takes one parameter: registration.  
* Return all military aircraft in the world. It takes no parameters.  

There are also other built-in aspects of the Firebase server including user and crash analytics.  

A further component of the system is the ADS-B Exchange API. This is hosted on RapidAPI, the world’s largest API marketplace. This is a paid API with a daily quota of 1,000 calls for 100nm calls and 500 calls for both 25nm calls and single aircraft lookup. This component is entirely controlled by ADS-B Exchange and RapidAPI.  

The client application is an Android app. When the user opens the app, the app calls a cloud function in Firebase. The cloud function then calls the ADS-B API. A JSON string is returned to Firebase. Firebase in turn sends this JSON string back to the mobile app which creates an instance of a Plane object for each JSON object. The mobile app then looks up the ICAO aircraft and airline codes in the Firestore database. If a key matches these codes, their value is returned to the app (full airline and aircraft names). The app then renders plane icons on the map.  

The AR operates similarly to the map activity, calling the same cloud function but with a distance parameter of 25nm rather than 100nm (by default, this can be changed).

### 2.2 - System Design Decisions
As shown in the diagram at the start of this section our app is a three tiered system.
The database (DB) tier is used to hold a list of aircraft and airline identifier codes that act as keys with their full names as the values. We decided to use a cloud based platform because it was the most suitable solution for our app. One of the main reasons why you would choose a cloud platform for a mobile app DB is that it is scalable. If more users are downloading the app and pulling information from the DB, the server is processing more requests. A cloud DB platform will scale to meet demand.  
We selected Firebase’s Firestore as our platform because it is easily integratable with Android Studio. We also liked that Firestore uses a NoSQL database as opposed to a relational. While relational databases might be suited for larger sets of data, they take more setting up. We only had a relatively small amount of data to store so a NoSQL database suited our needs better.  
  
Due to the nature of our project it is not possible to have all of the functional tier in one location. Some of our code is cloud based, and pulls data from the ADS-B exchange to pass it to the mobile client. One of the reasons we had this portion of code separated is for security. It contained our personal API key for the ADS-B exchange and we didn’t want this code being stored on the user’s device. Again we used Firebase as our cloud platform for its ease of use with Android Studio. We were also swayed by the fact that Firebase has inbuilt crash analytics, testing, and performance checking. All other functions are located on the client's device.  
  
The presentation tier is all located on the user’s device. Since our system is a mobile application, we had the everyday user in mind during UI design. We decided to build the app around a central feature - the flight map. This meant that upon starting the app the user would be presented with something they could instantly recognise and interact with.
We minimised switching between different screens because this can be an unnecessary distraction to any user; expert or not. Most features of the app can be accessed from the flight map screen with the exception of the AR component.  

***

## 3. High-Level Design

Below is a system context diagram showing how external actors interact with av8.
  
![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/technical_manual/images/context.png?raw=true "System Context Diagram")

The majority of interactions with the system are between users and the system, and the system and the ADS-B Exchange API.

For example, the user sends a request (by tapping the refresh button) to the system for all aircraft within 100nm of the user’s location. The system then sends the request with the user's location to the ADS-B API. The API sends a JSON to the system which parses it and presents it to the user.

The next diagram is a level one data flow diagram showing to separate data flows that occur in the app.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/technical_manual/images/dfd.png?raw=true "Level One Data Flow Diagram")

The first data flow details the data exchange that occurs between the user, the system and ADS-B Exchange. The second section of the diagram illustrates the change to the user's environment that occurs when they change one of their settings.

Below is a class diagram that represents the main classes that interact with the main activity.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/technical_manual/images/classes.png?raw=true "Class Diagram")

In this class diagram you can see a Plane object which is created by the APICaller is put in a set, and displayed on the map in the Main Activity. You can also see how the MilAPICaller & RegAPICaller inherit from APICaller.

Finally we have a state chart diagram to represent the changes in the app's state that occur in run time.

![alt text](https://github.com/NiallS4/av8-Flight-Tracker/blob/master/technical_manual/images/states.png?raw=true "State Chart Diagram")

***

## 4. Testing and Validation

### 4.1 User Tests

We carried out user testing from 1 to 5 March. This gave us enough time to take user feedback into account. Users were provided with a link to the app APK and our survey. We needed two types of users to take part in our tests; general and expert users. “Expert” users would be aviation enthusiasts.  

We contacted the DCU Aviation Society, in order to find expert users to test our app. These two groups allowed us to obtain a diverse range of opinions. As aviation enthusiasts would be highly likely to use other flight tracking apps, their opinions are of particular value.  

No users who took part noticed any bugs or crashes which was very positive for us. However they did provide important feedback and suggestions, some of which we had not thought of ourselves. For instance, one participant pointed out that the “army green” colour of the military aircraft icons blended in with the satellite and hybrid map types. One of the expert participants suggested that some way of representing altitude would be good as no flight tracking apps have such a feature. We implemented an altitude heat map based on this suggestion. Some suggestions like showing origin and destination are unfortunately not possible due to the limitations of the API.  

The results of our user tests are available in the *tests/User Tests* directory in CSV format.

### 4.2 Integration Tests

We carried out integration tests through the Firebase test lab. These tests analyse the structure on the app’s UI and simulates user activities on a real device. It generates a report, screenshots, a video and a crawl graph. It reports any errors and performance issues. We carried out these tests during the week of 23 February in advance of our user testing. We picked a wide range of devices to ensure that there were no issues. This was a possibility as we had already had to change our code earlier in the development based on a crash with our location code on an LG G6.  

We tested our app on five phones: Samsung Galaxy S8, OnePlus 6T, Google Pixel, Sony XZ2 and Xiamoi Mi Max 3. We also tested it on a tablet, a Samsung SM-T837V. These devices used a variety of API levels to ensure we covered as wide a range of situations as possible. The results of these tests showed no crashes and the app worked well on all devices. Sometimes high input latency occurred on the map, mainly due to the delay in receiving the result from the API call. There is little we can do about this as we cannot make the API any faster.

### 4.3 Instrumentation Tests

We also undertook UI instrumentation tests by using Espresso in Android Studio. Espresso is a testing framework for creating user interface tests. With Espresso we were able to record our actions interacting with the app on our phones. Espresso creates tests based on these actions and ensures that there are no errors. It checks to make sure that the test actions synchronise with the application’s user interface.

### 4.4 Unit Tests

For our unit testing we used JUnit 4 as our platform. We aimed for high coverage and all of our methods were at least tested for operability. Our testing was organised according to Android test fundamentals. We used the *androidTest* directory for our unit tests of activity classes. The MainActivity and ARActivity classes had to be tested on actual devices or emulators. We ensured that all elements of each class were successfully created. Other classes such as Plane and ARHelper which are not activity classes were tested from the *test* directory, as they can be run within Android Studio without an actual device or emulator. In general we did not find any issues with our functions. Our unit tests did however reveal a bug in the heightGenerator function in the ARHelper class. If the distance is less than 1,000 it should return a random float between 1 and 2, but an error meant that it returned just 2.

### 4.5 Regression Tests

As we added additional features during the last week, we added additional unit tests to cover these areas. We ran two more integration tests through the Firebase test lab to ensure that these new features had not introduced any new errors. These tests were on an LG G6 and Google Pixel. Both completed successfully. We also re-ran all our existing unit and instrumentation tests, as well as adding new ones to test the new features.

***

## 5. Problems and Resolution

#### Extra charges for API usage exceeding the quota

**Problem**: The API we chose to use ADS-B Exchange on RapidAPI, charges for usage exceeding the quota. This is 1,000 calls a day for 100nm queries, and 500 calls a day for each of 25nm queries and single aircraft lookup. While this quota is fairly generous, we still had to take this into account when designing the app. We had hoped to implement auto-refreshing for the map but this could cause issues if someone left the app open for an extended period of time. In addition, initially the code in the AR that obtained the user location would call the API each time a (minor) change in location was detected.

**Resolution**: We decided not to include auto-refreshing and instead have a manual refresh button. While this is not quite as user friendly as the map refreshing by itself, we felt that this was the only realistic solution. No users who took part in our user tests complained about the refresh button. The issue with repeated API calls in the AR was resolved by only checking the current location once.

#### Setting up the cloud functions on Firebase

**Problem**: During the first week of development, we set up a basic map with default Google Map markers representing each plane. We called the API directly from the client using OkHttp. However, we had always planned to use a server as an intermediary to call the API. We decided to use Firebase cloud functions to achieve this. We initially found the cloud functions confusing, both in setting up and writing. We both had very limited experience of JavaScript which the cloud functions use.

**Resolution**: We decided to first start with a very simple JavaScript function that simply added two numbers together and returned this result to the app. We read the Firebase documentation on how to initialise and deploy the cloud functions. We were able to get this function to return the result to the app. We were able to use this as a basis for the API call cloud function. We still had difficulty getting it to work but eventually resolved it by following online guides.

#### The default Google Maps infobox only allowing a small number of characters

**Problem**: The default Google Maps infobox which displays when the user taps on a marker can only display one short sentence. We wanted to show extensive details on each aircraft so this would have to be redesigned.

**Resolution**: From researching online, we discovered that it was possible to create a custom infobox. We were able to implement this in our app by following various online tutorials.

#### Representing a set of coordinates as a marker within the AR scene

**Problem**: Having set up a basic AR scene, we needed to render markers at actual GPS coordinates rather than at fixed locations on the user's screen.

**Resolution**: We read extensively on using coordinates in ARCore. We found that most apps that display real world coordinates as AR renderables use the ARCore-Location library by the British company Appoly. Using this library, we were able to create markers, representing hardcoded coordinates. This helped us make a good start at the AR but it was still very difficult to get this to work with the API and to account for distance and altitude.

#### Overlapping marker in the AR scene

**Problem**: When we had a working prototype of the AR, we encountered an issue whereby lots of markers would overlap making it hard to read. This may not have been an issue in many locations, but as we are near Dublin Airport and prospective users would be most likely to use this component near an airport, this needed to be fixed.

**Resolution**: We were able to resolve this problem by scaling the plane markers based on distance from the user. The marker renderable are reduced in size based on how far away they are. This took some trial and error to get right. We had to ensure that markers were not so small that they would not be difficult to read.

#### Oversensitivity in Device Sensors

**Problem**: The compass feature in the map and AR component rely on the mobile device’s accelerometer and magnetometer to locate true North. The problem is that the sensors will detect even the slightest change in phone orientation or local magnetic fields, leading to the compass being quite jumpy and distracting.

**Resolution**: To counter this we designed the compass to distract the user as little as possible. For the map compass, the compass headings will only move if there is a significant change in orientation (> 4 degrees). For the AR the compass was designed to only show the user’s orientation as degrees away from north (e.g. 0 would be north, 180 would be south).

***

## 6. Differences from Functional Specification

We were pleased that we were able to implement almost all of our requirements from the functional specification. 
The only requirements we were unable to implement were the flight statistics and the related database. This was majorly due to the financial cost of using any of the API’s with accurate flight information that could be used for statistics. It was not practical to try and implement flight statistics with the ADS-B API because we would have to be calling the API 24/7, and would quickly go over our daily quota leading to significant charges.

However we did add some extra features which we did not have in the requirements.
We added some minor features such as a custom compass and the ability to show all military aircraft in the world. 
Some major extra features we added includes aircraft filtering which allows the user to only display aircraft based on a certain criteria. We also added the ability to search for an aircraft with a particular registration anywhere in the world. Finally we added altitude and speed heat maps which give the user interesting visualisations on real time flights.

There were some changes to the system architecture, but nothing that drastically changed the system layout. We no longer needed a flight statistics API. The web server and web application became integrated because of our use of Firebase instead of Django & Apache. The database we used was a NoSQL database rather than a SQL one as we originally anticipated.

***

## 7. Installation Guide

The av8 app requires an Android device running a minimum operating system of 7.0 (“Nougat”). The app will not work on devices running older versions. The AR component requires that the device supports Google Play Services for AR (ARCore). A list of compatible devices is available here: https://developers.google.com/ar/discover/supported-devices  
A small minority of devices require a minimum of Android 8.0 or 9.0 to use AR (see above link for details). If your device does not support ARCore, all other app features will still work.  

To install the app, first ensure that installing apps from unknown sources is enabled. It should be possible to find this option in the device settings by searching for it. Otherwise a web search with your device and/or Android OS version should tell you where to find this setting.  
Following this, just download the file *av8.apk* and tap on the downloaded file and select “install” when prompted.  

It is necessary to grant location and camera access to make use of the app’s full functionality (including the AR).
