---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T06:42:29
updated: 2026-06-13T06:42:29
status: active
tags:
  - memoc
  - memoc/worklog
---
# Disable screen shake on game over for cleaner restart UI

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T06:42:29

## Summary

- Removed `triggerShake` call in `triggerGameOver()` function in `MainScene.kt` to disable screen shaking during the game-over screen.

## Changed Files

- `.memoc/02-current-project-state.md`
- `.memoc/session-summary.md`
- `app/src/main/java/com/example/dashhero/game/scene/MainScene.kt`

## Verification

- Ran `.\gradlew.bat test` (All tests passed).
- Verified compilation with `.\gradlew.bat assembleDebug`.

## Follow-up

_None._

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
