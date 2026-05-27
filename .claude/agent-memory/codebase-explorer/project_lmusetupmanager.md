---
name: project-lmusetupmanager
description: Architecture snapshot of the LMU Setup Manager Android app — stack, modules, conventions, gotchas
metadata:
  type: project
---

LMU Setup Manager is a single-module Android app (Kotlin + Jetpack Compose) for managing racing car setups for Le Mans Ultimate (LMGT3 class).

**Why:** Personal tool for managing sim-racing car setups across tracks and conditions.

**How to apply:** All new features go in `:app`. Data is static (no network). New cars/tracks/parameters = edit `data/static/` files only. New screens = add a `Screen` sealed class entry + `NavHost` composable + a `@HiltViewModel`.

Key facts:
- Package: `com.lmu.setupmanager`, minSdk 26, targetSdk/compileSdk 35
- AGP 8.7.0, Kotlin 2.0.21, KSP 2.0.21-1.0.28
- Stack: Compose BOM 2024.11.00, Hilt 2.52, Room 2.6.1, Navigation Compose 2.8.4, kotlinx.serialization 1.7.3
- No Retrofit — no network layer
- No README, no CI, no CLAUDE.md, no .cursorrules as of initial commit
- `SetupViewModel` has in-memory undo/redo (ArrayDeque, MAX_HISTORY=50)
- `Setup.values` is `Map<String,Float>` — corner-specific params use `key_FL/FR/RL/RR` compound keys
- Room stores `valuesJson` as serialized JSON (kotlinx.serialization); `Converters.kt` holds `toDomain`/`toEntity` extension fns
- DI: two Hilt modules in `AppModule.kt` — `DatabaseModule` (object, @Provides) + `RepositoryModule` (abstract, @Binds)
- `FeedbackItem` / `ResolvedAdjustment` domain models exist but no ViewModel/Screen uses them yet — unimplemented feature
- Theme: Material3 with dynamic color (Android 12+), falls back to light/dark scheme
- Only unit test: `BuildDefaultValuesUseCaseTest` (JUnit4 + coroutines-test)
