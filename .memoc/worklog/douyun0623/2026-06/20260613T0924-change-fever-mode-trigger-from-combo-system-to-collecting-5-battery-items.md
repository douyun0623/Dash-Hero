---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T09:24:46
updated: 2026-06-13T09:24:46
status: active
tags:
  - memoc
  - memoc/worklog
---
# Change Fever Mode trigger from combo system to collecting 5 battery items

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T09:24:46

## Summary

- Removed the combo count/timer properties and addCombo/triggerFever logic from Player.
- Implemented collectBattery() in Player, which increments feverBatteryCount when not in Fever, triggering a 5s Fever mode upon reaching 5 batteries.
- Replaced the combo UI with a premium top-left cyan Battery indicator gauge in MainScene.

## Changed Files

- `app/src/main/java/com/example/dashhero/game/objects/Player.kt`
- `app/src/main/java/com/example/dashhero/game/scene/MainScene.kt`

## Verification

- Ran `.\gradlew.bat test` to verify compilation and passing tests.

## Follow-up

_None._

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
