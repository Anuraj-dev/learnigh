# Learnigh

**Track bought & free courses, deadlines, and where to learn** — a native Android personal learning tracker.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/minSdk-26-green)
![License](https://img.shields.io/badge/license-MIT-blue)

Package: `com.anuraj.learnigh` · Repo: [Anuraj-dev/learnigh](https://github.com/Anuraj-dev/learnigh)

## Features
- Offline-first **Room** database (no login, no paywall)
- Track **paid courses**, **subscriptions**, **YouTube**, **free** resources
- **Home dashboard** — due soon, in progress, streak-lite
- **Course list** with search + status/source filters
- **Add / Edit / Detail** with progress slider & open-link
- **Deadlines** calendar list
- Dark premium **indigo/violet** Material 3 UI
- Seeded sample courses (Udemy, YouTube, Coursera, freeCodeCamp, Domestika, Notion)

## Stack
- Kotlin · Jetpack Compose · Material 3
- Room · Navigation Compose · ViewModel · DataStore
- Single-module `app` · minSdk 26 · targetSdk 34 · compileSdk 34

## Open in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) (Ladybug / Koala+ recommended) with Android SDK 34 and a device emulator (or use a physical device).
2. **File → Open** → select this repo root (`learnigh/`).
3. Wait for Gradle sync. If prompted, create/accept `local.properties` with your SDK path, e.g.:
   ```properties
   sdk.dir=/Users/YOU/Library/Android/sdk
   ```
   (Windows: `C:\\Users\\YOU\\AppData\\Local\\Android\\Sdk` · Linux: `/home/YOU/Android/Sdk`)
4. Select an emulator or USB device → click **Run** ▶ (`app`).

### CLI build
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Project layout
```
app/src/main/java/com/anuraj/learnigh/
  data/          Room entity, DAO, DB, seed, DataStore, repository
  ui/            theme, home, courses, detail, edit, calendar, settings, navigation
  viewmodel/     Home, Courses, Detail, Edit, Calendar, Settings
  util/          Date helpers
PRODUCT.md       Product vision
opencode.json    OpenCode Space Bunny config (model + variant max)
```

## OpenCode
```bash
PATH=/home/box/.local/bin:$PATH
opencode run --auto -m opencode/space-bunny-free --variant max "…"
```

## License
MIT © Anuraj (Raja) Saikia
