---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T14:02:44
updated: 2026-06-13T14:02:44
status: active
tags:
  - memoc
  - memoc/worklog
---
# Improve enemy stomp collision logic to prevent back corner deaths

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T14:02:44

## Summary

- Modified `MainScene.kt` to simplify stomp classification for both ground enemies and drone enemies.
- Replaced the edge-case prone horizontal-vertical overlap ratio checks (`overlapY < overlapX * 1.5f`) with a clean midpoint check (`playerBB.bottom <= enemyBB.centerY()`).
- Stomping will now trigger correctly if the player is descending relative to the enemy and lands anywhere in the top half of the enemy.

## Changed Files

- `app/src/main/java/com/example/dashhero/game/scene/MainScene.kt`

## Verification

- Ran `.\gradlew.bat test` to verify build succeeds.

## Follow-up

_None._

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
