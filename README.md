# Where to go

A prototype Android app for Te Rawhiti Community Mental Health Centre. It keeps a phone copy of the clinic’s where-to-go list, for people using the service and for people supporting them. The directory and the group handout open without internet.

This is not a Health New Zealand product, and it does not give medical advice. If someone is in danger, call 111.

## What’s in this folder

`WhereToGo` is the Android project. The other files are the sources it was assembled from:

- `IMG_20261102_144514.jpg` is the Te Rawhiti “Where to go?” poster. Its QR code opens https://sites.google.com/view/terawhiti-wheretogo
- `scan_1793755332.pdf` is the group handout, *Treating Myself Well*. A copy is in the app as `WhereToGo/app/src/main/assets/treating-myself-well.pdf`
- `faqs-disability-special-considerations-flag.pdf` is the disability special-considerations note kept with these sources
- `HealthNZ.html`, `OpenStreetMap.html`, `firstport.html`, `claybeds.html`, `starlink.html`, and `SimplyAble.html` are saved pages. SimplyAble is the same save as Claybeds

The Guide tab in the app follows the handout. Answers stay on the phone until they are cleared. The original scan can be opened from that screen.

## Build

Android Studio is installed, and the SDK used for this project is in `/Applications` (`platform-tools`, `platforms`, and `build-tools`). `WhereToGo/local.properties` points at that SDK and is not committed.

From `WhereToGo`:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
```

The debug APK is written to `WhereToGo/app/build/outputs/apk/debug/app-debug.apk`. Install it with Android Studio, or with `adb install` when a phone is connected.

On another machine, create `WhereToGo/local.properties` with your SDK path:

```properties
sdk.dir=/path/to/Android/sdk
```

The app is Kotlin and Jetpack Compose. `minSdk` is 26. `compileSdk` is 37.

## Licence

The app source code is under the MIT Licence in `LICENCE`. Saved pages, the handout, images, service descriptions, and phone numbers are not covered by that licence.
