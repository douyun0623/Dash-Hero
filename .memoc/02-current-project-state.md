---
memoc: true
type: state
scope: project-memory
created: 2026-06-13T03:02:05
updated: 2026-06-13T03:02:05
status: active
tags:
  - memoc
  - memoc/state
---
# Current Project State

Last synced: 2026-06-13T03:02:05

## Current Status

- Core gameplay features, extension features (TitleScene, best score, pause menu, battery collectibles, and flying DroneEnemy) are fully implemented.
- Resolved camera-scroll death via a post-dash invincibility buffer (0.5s) and balanced flying drone spawning height and drift to eliminate unfair game layouts.

## Project Snapshot

<!-- memoc:snapshot:start -->
- Last synced: 2026-06-13T06:09:31
- Detected stack: Not detected

### Config Files

- `build.gradle.kts`

### Source Directories

- `.claude`
- `.gradle`
- `.idea`
- `.kotlin`
- `a2dg`
- `app`
- `docs`
- `gradle`
<!-- memoc:snapshot:end -->

## Open Tasks

- [x] Stabilize collision detection (lateral vs. vertical stomp collision).
- [x] Add more platforms and obstacle patterns to `PlatformManager`.
- [x] Implement audio feedback / sound effects interface.

## Completed Tasks

See `.memoc/worklog/` for full shared activity history.

## Commands

- Run debug build: `.\gradlew.bat assembleDebug` (Windows)
- Run unit tests: `.\gradlew.bat test` (Windows)

## Notes

- `a2dg` framework is structured as a separate Android Library project module, containing game loops, resources, basic scenes, and objects.
- `app` contains the gameplay logic specific to Dash Hero.

## Change Log

See `.memoc/worklog/` and generated `.memoc/activity.md`.
