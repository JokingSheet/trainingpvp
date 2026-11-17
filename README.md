# Movement Academy — Fabric 1.21.11

A client-side Fabric mod for movement practice, parkour routines, building drills, reaction exercises and a lightweight client tick monitor.

## Features
- Custom dark/neon academy GUI (press **H** in-game).
- Movement Lab with sprint, jump, bridging and landing drills.
- Movement Kit button: adds building and mobility supplies without clearing inventory.
- Reaction Trainer with a random green-light prompt, early-press detection and best-time tracking.
- Client tick monitor with a one-second rolling estimate.
- Session statistics. No telemetry, accounts, or external network calls from the mod.

## Build
Requires Java 21 and Gradle 9.2.1 or newer.

```bash
gradle build
```

The remapped mod jar is created in `build/libs/`.

## Install
1. Install Fabric Loader for Minecraft **1.21.11**.
2. Put the built `movement-academy-1.0.0.jar` into your Minecraft `mods` folder.
3. Start the Fabric 1.21.11 profile and press **H** to open the academy.

Fabric API is a required dependency.

## GitHub Actions
Every push runs the build workflow and uploads the built JAR as an Actions artifact.
