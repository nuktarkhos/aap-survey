# Adopt a Path Surveyor

## Introduction

Adopt a Path survey app for Android devices. It is targeted at Android 8.0 (SDK 26), but it could probably be retargeted at an earlier version of Android relatively simply.

## Design

Based on an older version, the user interface has been redesigned to be more modern. Data is either stored within a database managed through Android Room, or directly within app storage. Issue adding and editing should probably be changed to have a ViewModel to make that code cleaner and simpler, but it works as it is.

The data stored allows for multiple surveys to be run at once, but the user interface doesn't currently provide for this. Adding another UI element to allow adding/selecting surveys would be straightforward, though.

## Build

Import the project into [Android Studio](https://developer.android.com/studio/) and build from there.

## Install

Either [build](https://developer.android.com/studio/run) and [sign]() within Android Studio, then push to your phone; or download the prebuilt and signed release from [here](https://github.com/nuktarkhos/aap-survey/releases), and [sideload](https://www.xda-developers.com/how-to-sideload-install-android-app-apk/) it to your phone.

## Usage

Enter survey details on start - these details will be cached, and if a survey is in progress (app crashed, or phone died) then you can complete that survey.

On starting/continuing a survey, the screen will display the current location (both lat/lng and OS National Grid) and a list of current issues. Pressing the add icon lets you add an issue for the current location. Selecting an issue from the list lets you change details, including photo, but not the location or time of the issue. Issues can be deleted from the list by swiping them left.

When adding an issue, time and location will be automatically set. Issue type and status can be selected, and notes for the issue added. A photo needs to be added for each issue, and can be taken by pressing the camera icon. If the photo doesn't come out as you wanted, then pressing the photo lets you take another one to replace it. Finally, pressing the save button saves the issue to the list of issues, and returns you to the list.

Completing a survey is done by selecting the options menu in the top right and pressing "Finish survey". This will save all the issue information in an HTML file in the Downloads folder. Images are embedded in the file, so it is only one file but may be quite large depending on how many photos are in the survey. This file can then be copied off the phone and used to create the issues in the AAP web app. The photos themselves can either be copy/pasted from the HTML page, or saved locally (right click -> "Save image as") before being uploaded to the AAP website.

## FAQ

### Has the app been thoroughly tested?

Nope.

### I've found a bug, what should I do?

Report it on the Issues page above.

### Can the app be integrated with the website?

Yes, but this would be quite complex, and any changes to the website would run the risk of breaking the integration with the Android app. As such, it is simpler to still have issue entry remaining completely manual.
