# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**LMU Setup Manager** — Android app for managing LMGT3 car setups for Le Mans Ultimate / WEC/IMSA races.

- Package: `com.lmu.setupmanager`
- minSdk: 26 | compileSdk/targetSdk: 35
- Java toolchain: 17

## Stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose (sealed `Screen` class in `navigation/AppNavigation.kt`) |
| DI | Hilt 2.52 + KSP |
| Local DB | Room 2.6.1 |
| Async | Coroutines + Flow |
| State | ViewModel + `lifecycle-runtime-compose` |
| Serialization | `kotlinx.serialization-json` (Room `SetupEntity.valuesJson`, `Setup` export/import, Wizard route arg encoding) |

No network layer — all car/track/parameter data is hardcoded static Kotlin objects in `data/static/`.

## Build commands

```bash
./gradlew assembleDebug
./gradlew test                  # unit tests
./gradlew connectedAndroidTest  # instrumented tests (device/emulator required)
./gradlew lint
```

## Architecture patterns

- **UiState**: each screen has a `*UiState` data class; ViewModel exposes `StateFlow<*UiState>` via `stateIn(WhileSubscribed(5000))` or `asStateFlow()`.
- **One-shot events** (save success, error): stored as flags/nullable in UiState, consumed via `consumeX()` methods that reset them to `false`/`null`. Do not replace with `SharedFlow`/`Channel`.
- **Static data** (`data/static/`): top-level `val` lists imported directly by screens and use cases — not injected via Hilt.
- **DI split**: `DatabaseModule` (object, `@Provides`) and `RepositoryModule` (abstract class, `@Binds`) both live in `di/AppModule.kt`.
- **ThemeViewModel**: obtained via `hiltViewModel()` in `MainActivity`, then passed explicitly as a parameter through `AppNavigation` → `HomeScreen`. It is **not** retrieved via `hiltViewModel()` inside `HomeScreen`.
- **Existing screens**: `Screen.Diff` (`SetupDiffScreen`) and `Screen.Wizard` (`WizardScreen`) already exist — do not recreate them.

## Conventions

- **Corner-specific parameter keys**: compound format `"${paramKey}_$corner"` where `corner ∈ {FL, FR, RL, RR}`. Applied in `CornerGrid.kt` and `BuildDefaultValuesUseCase`.
- `kotlin.code.style=official` (set in `gradle.properties`).
- No XML layouts — 100% Compose.

## Smart Adjustment / Feedback pipeline — complete

All pieces are implemented:

- `FeedbackItem`, `ResolvedAdjustment`, `SelectedSymptom` models
- `FeedbackItems.kt` static data; `ResolveAdjustmentsUseCase`
- `WizardScreen` + `WizardViewModel` (launched via FAB in `SetupScreen` → `Screen.Wizard`)
- `FeedbackScreen` + `FeedbackViewModel` in `ui/feedback/` — standalone entry from `HomeScreen` ("Smart Feedback Wizard" button) → car selection → track selection → `Screen.Wizard`
- `Screen.Feedback` route in `AppNavigation.kt`

Do not remove any existing feedback/wizard code.

## Known issues / gotchas

1. **`SetupViewModel` car init**: reads `carId` from `SavedStateHandle` (injected by Hilt from the navigation back stack). Fallback `"bmw-m4-gt3"` applies only in unit tests where no real navigation exists.
2. **`BuildDefaultValuesUseCase` direct call in `SetupScreen`**: used inside `remember` to compute default values for modified-count badges — bypasses Hilt injection (intentional workaround, not a bug).
3. **Room**: `exportSchema = false`, DB version 1, no migrations. Any schema change needs `.addMigrations(...)` in `AppModule.kt` or a destructive migration.
4. **`SetupEntity.conditions`**: stored as enum name string via `ConditionsConverter` (`@TypeConverters` on `SetupDatabase`). Renaming enum constants requires updating `ConditionsConverter` — the storage format stays the same, no migration needed.

## Extending static data

| Task | File |
|---|---|
| Add a car | `data/static/Cars.kt` → append to `lmgt3Cars` |
| Add a setup parameter | `data/static/Parameters.kt` → add to the relevant category list |
| Add a track | `data/static/Tracks.kt` → append to `tracks` |
| Add feedback symptoms | `data/static/FeedbackItems.kt` → append `FeedbackItem` entries |

## Feature backlog

### ✅ Completate
- Fix SavedSetupsScreen: tapping un setup apre l'editor con i valori caricati
- Delete setup (`deleteSetupById` in DAO + repository + conferma UI)
- Export setup: share intent JSON via `ExportSetupUseCase`
- Import setup: file picker JSON via `ImportSetupUseCase` + Snackbar feedback

### 📋 In pianificazione
- **Duplicate setup**: copia un setup esistente assegnando nuovo UUID e label "Copy of X"
- **Rename setup**: modifica inline del label senza riaprire l'editor
- **Filter saved setups**: filtrare la lista per `carId` o `trackId`
- **Setup comparison**: visualizzare due setup affiancati con diff evidenziata
