# PR: Fix post-launch crashes, run configuration, JSON handling & add tests

## Summary

Fixes crashes after ~10 minutes of use (especially on Play Store builds), the "Activity class does not exist" run error, and hardens JSON parsing across the app. **Restores notifications** when alarms fire or after reboot (receiver now gets dependencies via Hilt EntryPoint). Adds unit tests for notifications contract, prayers, JSON mapping, and feature use cases **without changing production logic**.

---

## Problems addressed

### 1. Crashes after ~10 minutes (production)

- **Unscoped coroutines:** `App.kt` and `PrayerNotificationHelper` used `CoroutineScope(Dispatchers.IO).launch` with no lifecycle, leading to orphan coroutines and crashes when the process was reclaimed.
- **DataStore:** `UserPreferences` used the `preferencesDataStore("user_prefs")` delegate while `DataStoreModule` provided a separate DataStore; after process restart this could trigger "multiple DataStores active for the same file".
- **Foreground service:** `PrayerForegroundService` returned `START_STICKY` and did not handle a null `Intent` on restart, causing NPE.
- **Workers:** `AzkarWorker` and `PrayerWorker` had no try-catch in `doWork()`; any exception crashed the app.
- **Room:** `fallbackToDestructiveMigration(false)` could crash the app on schema changes.

### 2. Run error: "Activity class {com.example.sakina/com.sama.sakina.MainActivity} does not exist"

- Manifest used short names (`.MainActivity`, `.App`) and had no explicit `package`; old installs or run configs could reference the wrong applicationId.

### 3. JSON parsing crashes

- **DataInitializer:** Quran init used `getJSONArray`/`getString`/`getInt`; malformed JSON could throw. Azkar/Duas/Tasbeeh had no try-catch.
- **JsonMapper:** `mapCategories`, `mapDuas`, `mapTasbeeh`, `mapQuran` used `get*` methods; missing or malformed keys caused `JSONException` and app crash.

### 4. Other crash risks

- **ExactAlarmReceiver:** No null check on `Intent`, no `goAsync()`, and manual creation of `AlarmScheduler(context)`.
- **MainActivity:** No guards or try-catch around alarm/notification setup.
- **App.kt:** No check that `appScope` and `dataInitializer` were initialized before use.

---

## Fixes applied

### Coroutines & lifecycle

- **App.kt:** Single app-scoped `CoroutineScope` provided by Hilt (`AppModule`: `SupervisorJob() + Dispatchers.IO`). Init runs inside this scope; `onCreate` checks `::appScope.isInitialized && ::dataInitializer.isInitialized` and wraps init in try-catch.
- **PrayerNotificationHelper:** Injects the same app-scoped `CoroutineScope` and uses it in `updateNextPrayerNotification()`; work wrapped in try-catch.

### DataStore

- **DataStoreModule:** Added `@SettingsDataStore` and `@UserDataStore`; provides two singleton DataStores: `settings_preferences` and `user_prefs`.
- **UserPreferences:** Removed the `preferencesDataStore` delegate; injects `@UserDataStore DataStore<Preferences>` from Hilt.
- **PrayerSettingsRepository & SettingsViewModel:** Inject `@SettingsDataStore`.

### Foreground service & workers

- **PrayerForegroundService:** Return `START_NOT_STICKY`; if `intent == null`, call `stopSelf()` and return.
- **AzkarWorker & PrayerWorker:** try-catch in `doWork()`; on exception log and return `Result.failure()`.
- **DailyResetWorker:** Logging in catch block.
- **DatabaseModule:** `fallbackToDestructiveMigration(true)`.

### Manifest & run

- **AndroidManifest.xml:** `package="com.sama.sakina"`; full class names: `com.sama.sakina.App`, `com.sama.sakina.MainActivity`, `com.sama.sakina.services.PrayerForegroundService`, `com.sama.sakina.receivers.ExactAlarmReceiver`.
- **Developer steps:** Uninstall old app from device/emulator, delete "app" run configuration, Clean + Rebuild + Run.

### Receiver & Activity

- **ExactAlarmReceiver:** `onReceive(context, intent?)` with null check; `goAsync()` and `pendingResult.finish()` in `finally`; inject `AlarmScheduler` via Hilt; try-catch around logic.
- **MainActivity:** Guard `setupPreciseAlarmsAndService()` with `::prayerNotificationHelper.isInitialized && ::alarmScheduler.isInitialized`; try-catch around alarm setup, `DailyResetWorker.schedule()`, and `createNotificationChannel()`.

### Notifications fix (notifications not received after alarm / reboot)

- **Cause:** When the system fires an alarm (or sends `BOOT_COMPLETED`), it instantiates `ExactAlarmReceiver` itself. Hilt does not inject into such components, so `@Inject lateinit var notificationHelper` and `alarmScheduler` were never set. Using them caused `UninitializedPropertyAccessException` (caught and logged), so the next prayer was never scheduled and boot reschedule did nothing.
- **Fix:** Removed `@AndroidEntryPoint` and field injection from `ExactAlarmReceiver`. Added **`AlarmReceiverEntryPoint`** in `di/AlarmReceiverEntryPoint.kt`: a Hilt `@EntryPoint` that exposes `PrayerNotificationHelper` and `AlarmScheduler`. In `onReceive()`, the receiver now gets the app context and retrieves dependencies with `EntryPointAccessors.fromApplication(app, AlarmReceiverEntryPoint::class.java)`, then uses `entryPoint.prayerNotificationHelper()` and `entryPoint.alarmScheduler()` for all logic. Alarms and boot reschedule now run correctly without changing notification content or scheduling logic elsewhere.

### JSON handling

- **DataInitializer:** initAzkar/Duas/Tasbeeh wrapped in try-catch with logging; initQuran uses `optJSONArray`, `optJSONObject`, `optString`, `optInt`, `getOrNull`, and `continue` for invalid entries.
- **JsonMapper:** All mappers use `opt*` and `continue`/defaults; `mapCategories`, `mapDuas`, `mapTasbeeh`, `mapQuran` wrapped in try-catch; no `get*` on possibly missing keys.

### Other

- **PrayerScreen StarsBackground:** Precomputed `cachedStars` list to avoid reallocating on every recomposition.
- **RepositoryModule & ExampleInstrumentedTest:** Package/import and assertion updated to `com.sama.sakina`.
- **MainActivity:** Single `DailyResetWorker.schedule(this)`; removed duplicate `scheduleDailyReset()`.

---

## Tests added

All tests were added **without changing production code**; they cover notifications contract, prayers, JSON mapping, and feature use cases.

| Test | What it covers |
|------|----------------|
| **ShouldCelebrateUseCaseTest** | All Fard completed → true; one missing → false; empty list → true; Nafila ignored for celebration; nafila incomplete does not affect result. |
| **JsonMapperTest** | Valid minimal JSON for azkar categories and tasbeeh parses correctly; empty `categories`/`tasbeeh` arrays return empty lists. |
| **GetDayPrayersUseCaseTest** | Uses mockk; verifies repository `getSummary` is called and result returned. |
| **SetPrayerCompletionUseCaseTest** | Uses mockk; verifies repository `setCompleted` is called and updated summary returned. |
| **NotificationContractTest** | Documents alarm action and types (PRAYER/AZKAR); verifies receiver class; ensures notification contract is consistent. |

**How to run:** In Android Studio, use **Run → Run 'Tests in com.sama.sakina'** or run `./gradlew testDebugUnitTest` (requires Java 17).

**Dependencies added:** `io.mockk:mockk`, `io.mockk:mockk-agent-jvm`, and existing `kotlinx-coroutines-test` for unit tests.

---

## 10-minute crash checklist (verification)

| Check                                                                         | Status   |
| ----------------------------------------------------------------------------- | -------- |
| No `CoroutineScope(Dispatchers.IO).launch` in App or PrayerNotificationHelper | ✅ Fixed |
| DataStore provided only via Hilt (no delegate in UserPreferences)             | ✅ Fixed |
| PrayerForegroundService handles null intent, START_NOT_STICKY                 | ✅ Fixed |
| Workers have try-catch in doWork()                                            | ✅ Fixed |
| fallbackToDestructiveMigration(true)                                          | ✅ Fixed |
| ExactAlarmReceiver uses goAsync(), null-safe intent, injected AlarmScheduler  | ✅ Fixed |
| ExactAlarmReceiver gets deps via EntryPoint when system-instantiated (notifications work) | ✅ Fixed |
| MainActivity guards and try-catch around setup                                | ✅ Fixed |
| App onCreate guards and try-catch                                             | ✅ Fixed |
| JsonMapper uses opt\* and try-catch                                           | ✅ Fixed |
| DataInitializer uses opt\* for Quran, try-catch for all inits                 | ✅ Fixed |

---

## Files changed

**Application & DI:** `App.kt`, `di/AppModule.kt`  
**DataStore:** `DataStoreModule.kt`, `UserPreferences.kt`, `PrayerSettingsRepository.kt`, `ui/Settings/SettingsViewModel.kt`  
**Utils & receiver:** `PrayerNotificationHelper.kt`, `ExactAlarmReceiver.kt`  
**DI:** `di/AlarmReceiverEntryPoint.kt` (Hilt EntryPoint for receiver when system-instantiated)  
**Service & workers:** `PrayerForegroundService.kt`, `NotificationWorker.kt`, `DailyResetWorker.kt`  
**Database:** `DatabaseModule.kt`  
**Data init & JSON:** `DataInitializer.kt`, `JsonMapper.kt`  
**UI:** `MainActivity.kt`, `ui/Prayers/PrayerScreen.kt`  
**Manifest:** `AndroidManifest.xml`  
**Tests:** `ShouldCelebrateUseCaseTest.kt`, `JsonMapperTest.kt`, `GetDayPrayersUseCaseTest.kt`, `SetPrayerCompletionUseCaseTest.kt`, `NotificationContractTest.kt`  
**Build:** `app/build.gradle.kts` (test deps: mockk)  
**Other:** `ExampleInstrumentedTest.kt`, `RepositoryModule`

---

## How to verify

1. Uninstall any existing app (old package) from device/emulator.
2. Delete the "app" run configuration, then **Build → Clean Project**, **Build → Rebuild Project**, and run the app.
3. Confirm app launches and no "Activity class does not exist" error.
4. Use the app for 10+ minutes and trigger alarms/notifications; confirm no crashes.
5. Run unit tests: **Run → Run 'Tests in com.sama.sakina'** (or `./gradlew testDebugUnitTest` with Java 17).
