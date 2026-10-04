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

## Install

Download the latest APK from [GitHub Releases](https://github.com/Anuraj-dev/learnigh/releases) (asset `Learnigh-vX.Y.Z.apk`, for example `Learnigh-v0.1.0.apk`).

1. On your Android phone, enable installs from unknown sources (Settings → Apps → special access, or the prompt the installer shows).
2. Open the downloaded APK and install.
3. Learnigh is offline-first. No account.

Each push to `main` builds a debug APK and publishes it on that version's release. Opening a pull request on this repo bumps the version once (see below) so the next merge ships the new tag.

## Versioning

Semver lives in [`version.properties`](version.properties) (`VERSION_NAME=0.1.0`, `VERSION_CODE=1`) and is what `app/build.gradle.kts` ships.

On each **pull request opened** (not on every push, and not on `main` by itself), CI looks at the PR diff plus title/body and picks **one** bump:

| Decision | When | Example |
| --- | --- | --- |
| **patch** (+0.0.1) | fixes, chores, docs, CI, small tweaks | 0.1.0 → 0.1.1 |
| **minor** (+0.1.0) | features, new screens, new behavior | 0.1.1 → 0.2.0 |
| **major** (+1.0.0) | breaking: package/`applicationId` change, schema migration that drops data, removed public flow | 0.2.0 → 1.0.0 |

`VERSION_CODE` increments by 1 on every bump. The decision is explicit in [`.github/workflows/bump-version.yml`](.github/workflows/bump-version.yml): it asks OpenCode **MuseSpark 1.3** (`opencode/muse-spark-1.3-contributor-free`) to reply with exactly `patch`, `minor`, or `major` when that CLI is authenticated on the runner, and otherwise runs [`scripts/decide-bump.sh`](scripts/decide-bump.sh) on the same diff. [`scripts/bump-version.sh`](scripts/bump-version.sh) applies the word. The commit is pushed back to the PR branch. Fork PRs are skipped (no write access). Merging to `main` does not bump again; [`.github/workflows/release.yml`](.github/workflows/release.yml) only builds and publishes `Learnigh-vX.Y.Z.apk`.

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
opencode.json    OpenCode MuseSpark 1.3 config (model + variant max)
```

## OpenCode
```bash
PATH=/home/box/.local/bin:$PATH
opencode run --auto -m opencode/muse-spark-1.3-contributor-free --variant max "…"
```

## License
MIT © Anuraj (Raja) Saikia
