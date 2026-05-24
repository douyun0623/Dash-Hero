---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T06:09:44
updated: 2026-06-13T06:09:44
status: active
tags:
  - memoc
  - memoc/worklog
---
# Improve post-dash invincibility and balance drone spawn heights

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T06:09:44

## Summary

- Added a 0.5s post-dash invincibility window (`postDashInvincibleTime`) to bypass lateral collisions and prevent camera-scroll deaths.
- Lowered flying drone spawn heights and reduced vertical hover drift to balance game layouts and eliminate unfair obstacle setups.

## Changed Files

- `.memoc/02-current-project-state.md`
- `.memoc/session-summary.md`
- `app/src/main/java/com/example/dashhero/game/objects/DroneEnemy.kt`
- `app/src/main/java/com/example/dashhero/game/objects/PlatformManager.kt`
- `app/src/main/java/com/example/dashhero/game/objects/Player.kt`
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
