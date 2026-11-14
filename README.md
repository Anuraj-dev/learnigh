<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Learnigh logo" width="88" height="88" />
</p>

<h1 align="center">Learnigh</h1>

<p align="center">
  A personal learning tracker for Android. Keep your courses, playlists and subscriptions in one list.<br />
  See what is due this week, update your progress and open the link. Everything stays on your phone.
</p>

<p align="center">
  <a href="https://github.com/Anuraj-dev/learnigh/releases/latest">Download APK</a>
  ·
  <a href="#build-from-source">Build from source</a>
  ·
  <a href="#documentation">Docs</a>
</p>

Learnigh keeps track of what you are learning: paid courses you bought, app subscriptions that renew, YouTube playlists, free resources such as freeCodeCamp, and docs. Each course has a provider, a link, a status, a progress percentage and an optional deadline. The home screen lists what is due within seven days, overdue courses included.

It is a single-module Kotlin app built with Jetpack Compose and Material 3. Data lives in a local Room database. There is no account, no sync and no API key to set up. The app has no network code of its own, and it runs on Android 8.0 or newer.

## What you get

- **One list for everything you learn.** A course has a source type: paid course, app subscription, YouTube, website or other free. It also has a provider, a URL, a status, a progress percentage, notes and tags. The Courses tab sorts by nearest deadline, with courses that have no deadline last, then by most recent change.
- **Search and filters on the Courses tab.** Search matches title, provider and tags, ignoring case. Status and source chips narrow the list, and Clear all resets them.
- **A home screen for due dates.** Due soon lists courses that are not completed or expired and whose deadline is within seven days. Overdue courses stay in the list. Counters above it show in progress, due soon, completed and total.
- **Progress sets the status.** Moving the progress slider above zero turns a Not started or Wishlist course into In progress. Reaching 100 marks it Completed, and choosing Completed sets progress to 100.
- **Deadlines grouped by month.** The Deadlines tab lists open courses that have a deadline, under month headings, with the count and the next deadline at the top.
- **Sample courses on first run.** A fresh install adds six sample courses from Udemy, YouTube, Coursera, freeCodeCamp, Domestika and Notion. They are added once, and only when the course list is empty, so deleting them keeps them gone.
- **A streak that only goes up.** The Home screen shows an "N-day learning streak" that rises by one on each day you open the app. It never resets after a gap, so it counts days opened, not days in a row.
- **Reminder flags, no notifications yet.** Each course has a reminder switch that starts at the default set in Settings. A reminder shows as an alarm icon on the course card. Learnigh does not send notifications yet.

## Installation

| Method | Use it when |
| --- | --- |
| Install the APK | You want Learnigh on your phone. |
| Build from source | You want to change the code or run a debug build yourself. |

### Install the APK

1. Open the [latest release](https://github.com/Anuraj-dev/learnigh/releases/latest) on your phone and download the APK. It is named `Learnigh-vX.Y.Z.apk`, for example `Learnigh-v0.1.0.apk`.
2. Open the downloaded file. Android asks you to allow installs from the app you opened it with, such as your browser or file manager. Allow it, then install.
3. Open Learnigh. There is no account to create.

Release APKs are debug builds that GitHub Actions produces.

## Build from source

You need JDK 17 and the Android SDK with Platform 34 and build-tools 34.0.0. These are the versions CI installs.

### Open in Android Studio

1. In Android Studio, choose File, then Open, and select the repository root. That is the folder that contains `settings.gradle.kts`.
2. Let Gradle sync. If Android Studio does not create `local.properties` for you, copy the example file and set `sdk.dir` to your SDK path:

   ```sh
   cp local.properties.example local.properties
   ```

   On macOS the SDK is usually at `/Users/you/Library/Android/sdk`. On Linux it is usually at `/home/you/Android/Sdk`.

3. Pick an emulator or a phone connected over USB, then press Run.

### Build from the command line

Before the first build, set `ANDROID_HOME` or create `local.properties` with `sdk.dir`, as shown in [Open in Android Studio](#open-in-android-studio). Then run:

```sh
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Gradle writes the debug APK to `app/build/outputs/apk/debug/app-debug.apk`. The `adb install` step needs a phone with USB debugging turned on.

## How it works

Each screen is a Compose function that collects a `StateFlow` from its own ViewModel. The ViewModels read through `CourseRepository`, which wraps a Room DAO over the `courses` table. Settings hold the display name, the default reminder and the streak, and they live in a Preferences DataStore. The database is at version 2. The 1 to 2 migration adds an `app_metadata` table that records which sample data version has run. Room exports each schema to `app/schemas/`. The UI uses one dark Material 3 color scheme in indigo and violet.

The stack is Kotlin 1.9.24, Compose BOM 2024.06.00, Room 2.6.1, Navigation Compose 2.7.7 and DataStore 1.1.1. Min SDK is 26, and target and compile SDK are 34.

```
app/src/main/java/com/anuraj/learnigh/
  data/        Room entities, DAO and database, seed data, DataStore, enums, repository
  ui/          screens, components, navigation and theme
  viewmodel/   one ViewModel per screen
  util/        date helpers
```

## Releases and versions

Every push to `main` builds the debug APK and publishes it as a GitHub release. The tag is `v` plus the version in [`version.properties`](version.properties). With `VERSION_NAME=0.1.0`, the release is `v0.1.0` and its asset is `Learnigh-v0.1.0.apk`. If that tag already exists, the [release workflow](.github/workflows/release.yml) skips the publish.

Each pull request gets one version bump. When a pull request opens, the [bump workflow](.github/workflows/bump-version.yml) picks the bump with [`scripts/decide-bump.sh`](scripts/decide-bump.sh) from the PR title, body and diff. It applies the bump and pushes the commit to the PR branch.

| Bump | Used for | Example |
| --- | --- | --- |
| patch | Fixes, chores, docs, CI and small tweaks | 0.1.0 to 0.1.1 |
| minor | New features, new screens and new behavior | 0.1.1 to 0.2.0 |
| major | Breaking changes, such as a changed application ID, a migration that drops data or a removed screen | 0.2.0 to 1.0.0 |

`VERSION_CODE` goes up by one with every bump. Merging to `main` does not bump again. Pull requests from forks are skipped because CI cannot push to a fork, so bump `version.properties` in the fork before you merge.

## Documentation

- [Product vision](PRODUCT.md): the problem Learnigh solves, its principles, the course fields, the screens and the v1 non-goals.

## Contributing

Open pull requests from a branch in this repository, not from a fork, so CI can push the version bump to your branch. See [Releases and versions](#releases-and-versions) for what happens after a merge.

## License

Learnigh is licensed under the [MIT License](LICENSE).
