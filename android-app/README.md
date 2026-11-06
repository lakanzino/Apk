# منشی مطب – Clinic Secretary (Android, Kotlin, Room)

Offline Persian/RTL app for a clinic secretary. Data lives in a local SQLite database (Room).

## Open & run
1. Android Studio (Koala or newer) → *Open* → select the `android-app` folder.
2. Let Gradle sync (Android Studio creates the Gradle wrapper jar automatically).
3. Run on a device/emulator with Android 7.0+ (minSdk 24).

## Structure
- `data/Entities.kt` – `patients` and `settings` tables (+ `AppSettings` helper)
- `data/Daos.kt`, `data/AppDatabase.kt` – Room DAOs and database (`clinic.db`)
- `util/PersianDate.kt` – Shamsi date conversion + Persian digits (no external library)
- `MainViewModel.kt` – shared state (Flow → LiveData)
- `MainActivity.kt` – header (doctor, specialty, Shamsi date) + bottom navigation
- `ui/RegisterFragment.kt` – patient form
- `ui/PatientsFragment.kt`, `ui/PatientAdapter.kt` – list with column toggles (chips, saved in SharedPreferences)
- `ui/SettingsFragment.kt` – doctor name, specialty, default fee

## Persian date library
The project uses its own small converter, so it needs **no** extra dependency and works fully offline.
To use a library instead: add `maven("https://jitpack.io")` to `settings.gradle.kts` and
`implementation("com.github.samanzamani:PersianDate:1.7.1")` to `app/build.gradle.kts`.
