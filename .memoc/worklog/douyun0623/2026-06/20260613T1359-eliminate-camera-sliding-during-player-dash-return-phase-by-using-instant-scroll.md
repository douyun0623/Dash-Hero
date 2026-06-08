---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T13:59:01
updated: 2026-06-13T13:59:01
status: active
tags:
  - memoc
  - memoc/worklog
---
# Eliminate camera sliding during player dash return phase by using instant scroll

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T13:59:01

## Summary

- Modified `MainScene.kt` to separate `returnDistance` from the eased `pendingScrollDistance`.
- Applied `returnDistance` as an `instantScroll` that is added directly to `scrollStep` in the same frame.
- This links player return movement to the map scrolling with a 1:1 ratio, removing all lag/easing and completely eliminating the sliding/slipping effect.

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
