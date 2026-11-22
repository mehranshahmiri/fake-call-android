<p align="center"><img src="docs/banner.svg" alt="Fake Call" width="100%"></p>

An Android app that makes your phone ring with a **real system incoming call**, 60 seconds after you open it. The ringing screen is Android's own dialer, so it looks and behaves exactly like a genuine call (lock screen, notification, ringtone, answer and decline).

Useful for testing call UIs, demos, or politely leaving a conversation.

## How it works

```
open app ──► alarm set (60 s) ──► CallReceiver ──► TelecomManager.addNewIncomingCall
                                                          │
                                       FakeConnectionService (self-registered
                                       call-provider PhoneAccount)
                                                          │
                                       system dialer shows its real incoming-call UI
```

Earlier versions drew a custom call screen and notification; they never looked right. Handing the call to the system through the Telecom framework is what makes it indistinguishable.

- `MainActivity`: registers the calling account, schedules the alarm with `setAlarmClock`, shows a countdown
- `CallReceiver`: fires when the alarm goes off and raises the incoming call
- `FakeConnectionService`: the Telecom `ConnectionService` that answers or rejects the call
- `Caller`: **caller name, number and delay live here**

## Configure the caller

Edit `app/src/main/java/com/fakecall/app/Caller.kt`:

```kotlin
const val NAME = "Mom"
const val NUMBER = "5550100"
const val DELAY_MS = 60_000L
```

The calling account is labelled "Mobile" on purpose, so the call screen does not say "Fake Call".

## Build and install

Needs JDK 17 and the Android SDK (`local.properties` with `sdk.dir=...`, or `ANDROID_HOME`).

```sh
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## First run

The app registers a calling account that has to be enabled once. Either tap **Enable calling account** in the app and switch it on, or from a computer:

```sh
adb shell telecom set-phone-account-enabled com.fakecall.app/.FakeConnectionService fakecall 0
```

Then open the app. The call arrives after the countdown. Tap **Schedule again** for another.

## Notes and gotchas

- Needs the `USE_EXACT_ALARM` permission, or `setAlarmClock` crashes on launch (already in the manifest).
- Do not call `TelecomManager.getPhoneAccount`; it requires `READ_PHONE_NUMBERS`. That is why there is an "Enable calling account" button instead.
- Reopening the app while it is still running does not re-run `onCreate`, so no new call is scheduled. Force-stop it first, or tap **Schedule again**.
- Some phones block `adb screencap` while a call UI is up, so check state with `adb shell dumpsys telecom` instead.
- Tested on Android 15 (minSdk 26, targetSdk 34, AGP 8.9, Kotlin 2.2, Gradle 8.11).

## Responsible use

Use it for testing and light social convenience. Do not use it to deceive anyone in a way that could harm them.

## License

MIT, see [LICENSE](LICENSE).
