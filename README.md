# Where to go

A prototype Android app for Te Rawhiti Community Mental Health Centre. It keeps a phone copy of the clinic’s where-to-go list, for people using the service and for people supporting them.

This is not a Health New Zealand product, and it does not give medical advice. If someone is in danger, call 111.

The directory and the group handout open without internet. A location note, a relay note, and an after-hours note can be written on the phone with no signal. Nothing is sent unless the person taps share or SMS themselves.

## What the app does

- **Now** — who the phone is for, the handout, and the urgent lines already in the list.
- **Find** — search the same list.
- **Guide** — *Treating Myself Well*. Answers stay on the phone until they are cleared. A short kindness block sits at the bottom of this tab only, under the skills, and it is not a call button.
- **Locate** — one GPS fix (a fresh reading, or the last one the phone already stored), with coordinates, time, accuracy, and a plain-text note for SMS, radio, or a hand-off. Delete removes it. The app does not track in the background.
- **After hours** — call the clinic on weekdays, or **0800 775 222** outside those hours. That number is the one Te Rawhiti’s list gives for urgent mental health help. There is no district inbox in the saved files, so the screen does not invent one. Former district health boards are now part of Health New Zealand. This app does not message them.
- **Talk by phone** — phone and text lines already in the list. The app does not run the appointment.
- **Rural and low signal** — those same phone lines, distance to the clinic when a fix is saved, and labelled gaps where the saved files name no rural clinic.
- **Relay a message** — a prototype for passing a note on. In New Zealand Police radio codes, 10-5 means relay the message. **105** is the public Police non-emergency number. The screen does not contact Police. Danger is still 111.
- **Bot telehandling** — off until it is turned on. Fixed chips only: Call 111, 1737, Clinic, After hours, Open Find. It does not diagnose, suggest a dose, or read handout answers.
- **Connection check** — asks whether this phone has internet, then optionally tries a general web check. No Starlink API is called. Starlink is only a way to restore internet. It is not a health service.

Waiting notes stay in a list on Locate until the person shares or removes them. A connection does not send them.

## Offline and online

| | Without internet | Needs a connection |
| --- | --- | --- |
| Directory, guide, kindness chips | On the phone | |
| Call 111, clinic, after hours, 1737, 105 | The dialler opens. The mobile network still has to be able to place the call. Airplane mode blocks calls. | |
| GPS fix and the plain-text location note | Yes. Satellites do not need mobile data. | |
| Share, SMS, copy | Copy always. SMS can work without mobile data. Share uses whichever app the person picks. | |
| Map, directions, Starlink’s website, service websites | | Yes |
| Connection check | Reports that there is no internet | Reports the network the phone already has. It still does not call Starlink. |
| Bot chips | The chips and the fixed lines are on the phone | |

## Real and prototype

Real, from the saved list or from a public number named in the app: the directory entries, 111, the clinic number, 0800 775 222, 1737, and 105 as the public Police non-emergency number. 105 is not on the clinic poster.

Prototype, and labelled as such: the 10-5 relay screen (it does not file a Police report), the after-hours note (it is not delivered to a district service), rural gaps where no local service was in the files, video telehealth (not in the files; the app does not start a call of its own), bot telehandling (scripted chips, no online model), and the Starlink seam (`NoStarlinkUplink` — no dish API).

Youthline is shown as 0800 376 633, and Lifeline text as 4357, because those are the numbers those organisations publish. The where-to-go page had shortened or mistyped them. Other numbers follow the saved list.

## Repository layout

`WhereToGo/` is the Android project (Kotlin, Jetpack Compose, minSdk 26).

`sources/` is the material the app was assembled from. It is not covered by the MIT licence:

- `sources/poster/where-to-go-poster.jpg` — the Te Rawhiti “Where to go?” poster. Its QR code opens https://sites.google.com/view/terawhiti-wheretogo
- `sources/handouts/treating-myself-well-scan.pdf` — the group handout. The same file is copied into the app as `WhereToGo/app/src/main/assets/treating-myself-well.pdf`
- `sources/handouts/faqs-disability-special-considerations-flag.pdf` — the disability special-considerations note kept with these sources
- `sources/saved-pages/` — saved HTML that was not a duplicate (`HealthNZ.html`, `OpenStreetMap.html`, `claybeds.html`, and the OpenStreetMap page files)
- `sources/images/` — an unused stock emergency-symbol image. It is not an app icon and it is not a Health New Zealand mark.

No sample APK is committed. Debug and release APKs are build outputs.

## Build

You need JDK 17 or newer (JDK 21 is fine) and the Android SDK. Android Studio is one way to install the SDK. A command-line SDK is enough.

Point Gradle at the SDK with `WhereToGo/local.properties` (this file is not committed):

```properties
sdk.dir=/path/to/Android/sdk
```

On Linux that is often `$HOME/Android/Sdk`. On macOS it is often `$HOME/Library/Android/sdk`. Use forward slashes. You can set `ANDROID_HOME` to the same directory instead.

From `WhereToGo`:

```bash
export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

`JAVA_HOME` must be the JDK directory, not the `java` binary. Android Studio’s bundled JDK is another valid choice; the path depends on the machine, not on one Mac.

The debug APK is written to `WhereToGo/app/build/outputs/apk/debug/app-debug.apk`. Install it with Android Studio, or with `adb install -r` when a phone is connected.

`compileSdk` in this project is 37. `targetSdk` is 36. `minSdk` is 26. In the current SDK package list the platform is `platforms;android-37.0` (there is no `platforms;android-37` id).

## Privacy

Location is read only after a tap, once. The saved fix and any waiting note stay in app storage until they are deleted. Handout answers are stored separately and are not attached to bot or location notes. Backup of app data is off. The bot preference is on the phone.

## Licence

The app source code is under the MIT Licence in `LICENCE`. Saved pages, the handout, images, service descriptions, and phone numbers are not covered by that licence.
