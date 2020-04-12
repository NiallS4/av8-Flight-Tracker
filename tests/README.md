## Testing And Validation (Extract from Technical Manual)

### 1 User Tests

We carried out user testing from 1 to 5 March. This gave us enough time to take user feedback into account. Users were provided with a link to the app APK and our survey. We needed two types of users to take part in our tests; general and expert users. “Expert” users would be aviation enthusiasts.  

We contacted the DCU Aviation Society, in order to find expert users to test our app. These two groups allowed us to obtain a diverse range of opinions. As aviation enthusiasts would be highly likely to use other flight tracking apps, their opinions are of particular value.  

No users who took part noticed any bugs or crashes which was very positive for us. However they did provide important feedback and suggestions, some of which we had not thought of ourselves. For instance, one participant pointed out that the “army green” colour of the military aircraft icons blended in with the satellite and hybrid map types. One of the expert participants suggested that some way of representing altitude would be good as no flight tracking apps have such a feature. We implemented an altitude heat map based on this suggestion. Some suggestions like showing origin and destination are unfortunately not possible due to the limitations of the API.  

The results of our user tests are available in the *User Tests* directory in CSV format.

### 2 Integration Tests

We carried out integration tests through the Firebase test lab. These tests analyse the structure on the app’s UI and simulates user activities on a real device. It generates a report, screenshots, a video and a crawl graph. It reports any errors and performance issues. We carried out these tests during the week of 23 February in advance of our user testing. We picked a wide range of devices to ensure that there were no issues. This was a possibility as we had already had to change our code earlier in the development based on a crash with our location code on an LG G6.  

We tested our app on five phones: Samsung Galaxy S8, OnePlus 6T, Google Pixel, Sony XZ2 and Xiamoi Mi Max 3. We also tested it on a tablet, a Samsung SM-T837V. These devices used a variety of API levels to ensure we covered as wide a range of situations as possible. The results of these tests showed no crashes and the app worked well on all devices. Sometimes high input latency occurred on the map, mainly due to the delay in receiving the result from the API call. There is little we can do about this as we cannot make the API any faster.

### 3 Instrumentation Tests

We also undertook UI instrumentation tests by using Espresso in Android Studio. Espresso is a testing framework for creating user interface tests. With Espresso we were able to record our actions interacting with the app on our phones. Espresso creates tests based on these actions and ensures that there are no errors. It checks to make sure that the test actions synchronise with the application’s user interface.

### 4 Unit Tests

For our unit testing we used JUnit 4 as our platform. We aimed for high coverage and all of our methods were at least tested for operability. Our testing was organised according to Android test fundamentals. We used the *androidTest* directory for our unit tests of activity classes. The MainActivity and ARActivity classes had to be tested on actual devices or emulators. We ensured that all elements of each class were successfully created. Other classes such as Plane and ARHelper which are not activity classes were tested from the *test* directory, as they can be run within Android Studio without an actual device or emulator. In general we did not find any issues with our functions. Our unit tests did however reveal a bug in the heightGenerator function in the ARHelper class. If the distance is less than 1,000 it should return a random float between 1 and 2, but an error meant that it returned just 2.

### 5 Regression Tests

As we added additional features during the last week, we added additional unit tests to cover these areas. We ran two more integration tests through the Firebase test lab to ensure that these new features had not introduced any new errors. These tests were on an LG G6 and Google Pixel. Both completed successfully. We also re-ran all our existing unit and instrumentation tests, as well as adding new ones to test the new features.
