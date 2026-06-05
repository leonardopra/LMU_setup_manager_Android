# LMU Setup Manager

> Manage your LMGT3 car setups for Le Mans Ultimate — WEC & IMSA races.

![minSdk](https://img.shields.io/badge/minSdk-26-brightgreen)
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow)

## Screenshots

<!-- Add screenshots here -->

## About

LMU Setup Manager is an Android app to create, manage, and fine-tune LMGT3 car setups for Le Mans Ultimate (WEC/IMSA races). It is built entirely with Jetpack Compose and Material 3, following a modern, single-activity architecture. All data is persisted locally via Room, so the app works fully offline.

## Features

- Browse and edit all LMGT3 setup parameters (Aero, Suspension, Dampers, Brakes, Tyres, Differential, Transmission, Electronics, Fuel)
- Corner-specific parameters (FL/FR/RL/RR)
- Save, load, delete, export and import setups as JSON
- Track selection with per-track recommended values
- Smart Feedback Wizard: select driving symptoms → get suggested adjustments
- Fully offline, no account required

## Tech stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| DI | Hilt 2.52 + KSP |
| Local DB | Room 2.6.1 |
| Async | Coroutines + Flow |
| State | ViewModel + lifecycle-runtime-compose |
| Serialization | kotlinx.serialization-json |

## Getting started

### Requirements

- Android Studio Hedgehog or newer
- JDK 17
- minSdk 26

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/leonardopra/LMUsetupmanager.git
   ```
2. Open the project in Android Studio.
3. Run on a device or emulator (API 26+).

## Project structure

```
app/src/main/java/com/lmu/setupmanager/
├── data/          # Room entities, repository, static data
├── domain/        # use cases
├── ui/            # screens, viewmodels, components
├── navigation/    # navigation graph
└── di/            # dependency injection modules
```

## Roadmap

Planned features (see the backlog in [`CLAUDE.md`](CLAUDE.md)):

- **Duplicate setup**
- **Rename setup**
- **Filter saved setups** by car/track
- **Setup comparison** (side-by-side diff)

## Contributing

PRs welcome! Feel free to open an issue or submit a pull request.

## License

Released under the [MIT License](LICENSE).
