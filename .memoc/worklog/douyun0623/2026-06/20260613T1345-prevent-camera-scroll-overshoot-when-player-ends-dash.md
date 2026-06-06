---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T13:45:09
updated: 2026-06-13T13:45:09
status: active
tags:
  - memoc
  - memoc/worklog
---
# Prevent camera scroll overshoot when player ends dash

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T13:45:09

## Summary

- Handled the transition when a dash ends in `MainScene.kt`.
- Cleared `pendingScrollDistance` (set to `0f`) immediately on the frame when the player stops dashing.
- This prevents the screen from sliding forward or overshooting due to accumulated scroll easing queue, making the dash stop extremely snappy.

## Changed Files

- `app/src/main/java/com/example/dashhero/game/scene/MainScene.kt`

## Verification

- Ran `.\gradlew.bat test` to ensure successful compilation.

## Follow-up

_None._

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
