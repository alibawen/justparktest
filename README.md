FindPark
========

![Demo](screenshots/main_view.png =150x)
![Demo](screenshots/marker_selected.png =150x)
![Demo](screenshots/navigation_selected.png =150x)
![Demo](screenshots/navigation_intent.png =150x)
![Demo](screenshots/sliding_anchor.png =150x)
![Demo](screenshots/sliding_extended.png =150x)
![Demo](screenshots/search.png =150x)

Features
========
* Display nearest private parking spaces according to the given location
* Display information on a selected parking space
* Delegate navigation to Google Maps

Material Design
===============
Google recently promoted the use of [material design](http://www.google.com/design/spec/patterns/promoted-actions.html).
I wanted to try it, as the design is very clean and user friendly.
I also got my inspiration from the new version of the Android Google Maps.

To recreate the sliding layout, I used the [AndroidSlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel) from umano.

How I worked
============
First, I needed to collect data, to find what and how I could design the application.
I download and tried the new release of Google Maps and found it very simple and quite easy to handle, with good transitions.
Then I decided to set up a similar design and I read material design guidelines to have a general overview on how to design a material design app.
I had to update myself on the latest Android version (API 21 - Android Lollipop) and 
I decided to use the app-compat-v7 library to have new features available for the previous versions of Android.
After getting all the information needed, I set a list of task I had to complete.
I first tried to make a couple of requests to the API, with different values, and get the general XML nodes.
I also had to list the facilities (and I may have forgotten some). It would have been better if I had the XML and the Web Service specs, 
but I assume it was my job to figure out the params.
I wanted to use Android Studio (I previously used ADT Eclipse), which version wasn't stable when I began the project. It took me some time to handle 
the IDE, and I also had to update the project and the IDE several times before Android Studio 1.0.0 came out (very recently). Google declared Android
Studio as the main official Android IDE, so I guess it was worthy to test it out.

I wanted the final result to be something complete and usable.

Code
====
* I tried to make the architecture easy to maintain, so I split model-view-controller in different packages.
* All strings are extracted in the string.xml file
* The application needs an active internet connection to work.
* Gson from Google is used to parse the json, all the XML nodes are in the model package.
* All downloads are done in an AsyncTask.
* The main data is stored into a Fragment to prevent data loss resulting from the main Activity changes (screen lock, orientation, language, ...)
* An external application (Google Maps) is called to handle navigation.

What is missing
===============
Probably unit tests.