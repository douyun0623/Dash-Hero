---
memoc: true
type: worklog
scope: project-memory
created: 2026-06-13T09:43:31
updated: 2026-06-13T09:43:31
status: active
tags:
  - memoc
  - memoc/worklog
---
# Allow dash chaining to enable smooth continuous dashing

actor: douyun0623
actor_source: git config user.name
branch: main
status: done
created: 2026-06-13T09:43:31

## Summary

- Removed the `if (isDashing) return` check in `Player.dash()`.
- This allows the player to reset the dash timer back to `dashDuration` when tapping during an active dash, enabling smooth consecutive chaining of dashes.

## Changed Files

- `app/src/main/java/com/example/dashhero/game/objects/Player.kt`

## Verification

- Ran `.\gradlew.bat test` to verify no compilation issues.

## Follow-up

_None._

## Related

- [Activity](../../../activity.md)
- [Worklog](../../README.md)
- [Actor](../../../actors/douyun0623.md)
