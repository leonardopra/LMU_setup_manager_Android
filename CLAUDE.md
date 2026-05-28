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

## Active next feature: FeedbackScreen

The smart-adjustment pipeline is partially complete:

**Done** — `FeedbackItem`, `ResolvedAdjustment`, `SelectedSymptom` models; `FeedbackItems.kt` static data; `ResolveAdjustmentsUseCase`; `WizardScreen` + `WizardViewModel` (launched via FAB in `SetupScreen` → `Screen.Wizard`).

**Remaining** — a standalone `FeedbackScreen` accessible from main navigation, not tied to an open setup editor:
1. New `FeedbackViewModel` + `FeedbackScreen`
2. New `Screen.Feedback` route in `AppNavigation.kt`

Do not remove any existing feedback/wizard code.

## Known issues / gotchas

1. **`SetupViewModel` car init**: reads `carId` from `SavedStateHandle` (injected by Hilt from the navigation back stack). Fallback `"bmw-m4-gt3"` applies only in unit tests where no real navigation exists.
3. **`BuildDefaultValuesUseCase` direct call in `SetupScreen`**: used inside `remember` to compute default values for modified-count badges — bypasses Hilt injection (intentional workaround, not a bug).
4. **Room**: `exportSchema = false`, DB version 1, no migrations. Any schema change needs `.addMigrations(...)` in `AppModule.kt` or a destructive migration.
5. **`SetupEntity.conditions`**: stored as enum name string (no TypeConverter). Renaming the `Conditions` enum will silently break deserialization via `Conditions.valueOf(conditions)`.

## Extending static data

| Task | File |
|---|---|
| Add a car | `data/static/Cars.kt` → append to `lmgt3Cars` |
| Add a setup parameter | `data/static/Parameters.kt` → add to the relevant category list |
| Add a track | `data/static/Tracks.kt` → append to `tracks` |
| Add feedback symptoms | `data/static/FeedbackItems.kt` → append `FeedbackItem` entries |
