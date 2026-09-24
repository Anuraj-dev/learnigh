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

## Bunny session 2 findings
- SeedData already includes Udemy, YouTube, Coursera, and freeCodeCamp samples.
- No additional provider seed entries are needed for session 2.
- LearnighNavHost keeps the floating action button in the outer Material 3 Scaffold.
- The bottom NavigationBar is visible only on the four primary tab destinations.
- FAB visibility is limited to Home and Courses, avoiding detail and edit routes.
- HomeScreen already reserves additional bottom space in its LazyColumn.
- The existing Scaffold insets and content padding keep the FAB clear of the bottom navigation.
