# OpenCode / Space Bunny review notes

## Sessions
1. **Learnigh UI polish** (`opencode/space-bunny-free` · `--variant max` · `--auto`)  
   - Audited Room (`CourseEntity` / DAO / DB / seed) and all Compose screens.  
   - Premium indigo/violet polish across Home, Courses, Detail, Edit, Calendar, Settings, CourseCard.  
   - FAB/nav spacing, AutoMirrored icons, empty states.  
   - Unit test `SeedDataTest` + Room schema export under `app/schemas/`.  
   - Confirmed `:app:testDebugUnitTest` and compile green before session stalled on review loop (killed after ~23m).
2. **Executor parallel work** — full Compose+Room scaffold, Calendar LazyColumn sealed rows, EmptyState, `assembleDebug` APK, GitHub push.

## Seed coverage
Udemy · YouTube playlist · Coursera · freeCodeCamp · Domestika · Notion (app subscription).

## Build verified
```bash
source /workspace/learnigh/env.sh
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk (~16MB)
```
