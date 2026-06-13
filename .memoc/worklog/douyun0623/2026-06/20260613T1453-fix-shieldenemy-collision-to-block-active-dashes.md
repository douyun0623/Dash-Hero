---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T14:53:40
updated: 2026-06-13T14:53:40
status: active
tags:
  - memoc
  - memoc/worklog
---
# Fix ShieldEnemy collision to block active dashes

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T14:53:40

## Summary

- Fixed a bug where ShieldEnemy could be passed through transparently when dashing because player invincibility contains the active dashing flag.
- Modified the pass-through condition to check `(player.isInvincible && !player.isDashing) || player.isReturning`, ensuring active dashes are blocked by the shield and trigger game-over.

## Changed Files

- `app/src/main/java/com/example/dashhero/game/scene/MainScene.kt`

## Verification

- Verified successful builds using `.\gradlew.bat assembleDebug` and correct execution of tests via `.\gradlew.bat test`.

## Follow-up

- None.

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
