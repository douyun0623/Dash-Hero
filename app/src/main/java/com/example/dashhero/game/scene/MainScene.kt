package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.dashhero.R
import com.example.dashhero.game.objects.DashScrollBackground
import com.example.dashhero.game.objects.DashTrail
import com.example.dashhero.game.objects.DroneEnemy
import com.example.dashhero.game.objects.ParticleSystem
import com.example.dashhero.game.objects.PlatformManager
import com.example.dashhero.game.objects.Player
import com.example.dashhero.game.objects.Item
import com.example.dashhero.game.objects.ItemType
import com.example.dashhero.game.objects.Spike
import com.example.dashhero.game.sound.SoundEffects
import com.example.dashhero.game.util.HighScoreManager
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BG,
        PLATFORM,
        TRAIL,
        PLAYER,
        UI,
    }

    enum class State {
        RUNNING,
        GAME_OVER,
    }

    override val world = World(enumValues<Layer>())
    private var state = State.RUNNING

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 72f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(210, 224, 255)
        textAlign = Paint.Align.CENTER
        textSize = 34f
    }

    private val player = Player(180f, 400f) // 400f로 확실하게 상향 조정
    private val background = DashScrollBackground(gctx, R.drawable.bg_dash_city, BASE_BG_SPEED)
    private val platformManager = PlatformManager(gctx.metrics.width)
    private val dashTrail = DashTrail()
    private val particleSystem = ParticleSystem()
    private var shakeTimeLeft = 0f
    private var shakeIntensity = 0f
    private var pendingScrollDistance = 0f
    private var activeTrailStretch = 0f
    private var totalDistance = 0f
    private var wasFever = false

    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        textAlign = Paint.Align.CENTER
        textSize = 60f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 0f, Color.BLACK)
    }

    private val gameOverScorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        textAlign = Paint.Align.CENTER
        textSize = 48f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 0f, Color.BLACK)
    }

    private val pauseBtnRect = RectF()
    private val pauseBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(160, 255, 255, 255)
        style = Paint.Style.FILL
    }
    private val pauseSymbolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    
    
    
    

    init {
        world.add(background, Layer.BG)
        world.add(platformManager, Layer.PLATFORM)
        world.add(dashTrail, Layer.TRAIL)
        world.add(player, Layer.PLAYER)
        world.add(particleSystem, Layer.PLAYER)
    }

    override fun update(gctx: GameContext) {
        if (state == State.GAME_OVER) return

        if (shakeTimeLeft > 0f) {
            shakeTimeLeft -= gctx.frameTime
            if (shakeTimeLeft <= 0f) {
                shakeTimeLeft = 0f
            }
        }

        val beforePlayerX = player.screenX
        val wasDashing = player.isDashing
        background.speed = 0f
        
        // world.update 대신 수동 업데이트를 통해 인자 전달
        background.update(gctx)
        platformManager.update(gctx)
        platformManager.updateEnemies(gctx)
        platformManager.updateItems(player.screenX, player.screenY, player.isMagnetActive, gctx.frameTime)
        particleSystem.update(gctx)
        dashTrail.update(gctx)
        player.updateWithCollision(gctx, platformManager)
        
        // 플레이어의 실시간 화면 X 좌표를 동기화하여 너무 가까운 곳에 적 스폰 억제 (복귀 억까 방지)
        platformManager.playerScreenX = player.screenX
        // 피버 모드 상태를 PlatformManager에 동기화 (안전 패턴 강제용)
        platformManager.isFeverMode = player.isFever
        platformManager.updateSafeCooldown(gctx.frameTime)

        // 피버 종료 감지: 이전 프레임에 피버였는데 이번 프레임에 아닌 경우
        if (wasFever && !player.isFever) {
            // 잔여 스크롤 70% 감쇠
            pendingScrollDistance *= 0.3f
            // 피버 종료 후 2초간 안전 패턴 유지
            platformManager.startSafeCooldown(2.0f)
        }
        wasFever = player.isFever

        // 아이템 (배터리, 자석, 별)과의 충돌 판정
        for (item in platformManager.getItems()) {
            if (item.isAlive && player.collidesWith(item)) {
                item.collect()
                SoundEffects.playCollect()
                when (item.type) {
                    ItemType.BATTERY -> {
                        totalDistance += 1000f
                        player.collectBattery()
                    }
                    ItemType.MAGNET -> {
                        player.magnetTimeLeft = 6.0f
                    }
                    ItemType.STAR -> {
                        player.giantTimeLeft = 5.0f
                    }
                }
            }
        }

        // 적과의 충돌 판정
        val dt = gctx.frameTime
        val playerBB = player.getBoundingBox()
        for (enemy in platformManager.getEnemies()) {
            if (enemy.isAlive && player.collidesWith(enemy)) {
                val enemyBB = enemy.getBoundingBox()
                
                if (player.isDashing || player.isGiant) {
                    // 1. 대시 공격 또는 거대화 무적 파쇄 (처치)
                    enemy.die()
                    // 대시 타격 파티클 스폰 (주황색 & 보라색)
                    particleSystem.spawnExplosion(
                        enemy.x, enemy.y,
                        intArrayOf(Color.rgb(255, 110, 70), Color.rgb(120, 80, 200)),
                        25
                    )
                    // 대시 타격 화면 흔들림
                    triggerShake(0.18f, 22f)
                } else {
                    val overlapX = minOf(playerBB.right, enemyBB.right) - maxOf(playerBB.left, enemyBB.left)
                    val overlapY = minOf(playerBB.bottom, enemyBB.bottom) - maxOf(playerBB.top, enemyBB.top)
                    
                    val prevPlayerBottom = playerBB.bottom - player.currentVelocityY * dt
                    val prevEnemyTop = enemyBB.top - enemy.currentVelocityY * dt
                    
                    val wasAbove = prevPlayerBottom <= prevEnemyTop + 15f
                    val isFallingRelative = (player.currentVelocityY - enemy.currentVelocityY) > -100f
                    
                    if (isFallingRelative && (wasAbove || (overlapY < overlapX * 1.5f && playerBB.bottom <= enemyBB.centerY()))) {
                        // 2. 밟기 판정: 위에서 아래로 충돌했거나 밟기 영역 내일 때
                        player.bounce()
                        SoundEffects.playStomp()
                        // 밟기 파티클 스폰 (연한 노란색 & 보라색)
                        particleSystem.spawnExplosion(
                            enemy.x, enemyBB.top,
                            intArrayOf(Color.rgb(255, 225, 95), Color.rgb(120, 80, 200)),
                            15
                        )
                        // 밟기 화면 흔들림
                        triggerShake(0.12f, 10f)
                    } else if (player.isInvincible || player.isReturning) {
                        // 3. 복귀 중이거나 대시 직후 무적 상태일 때는 옆면 충돌 무시 (반투명 패스스루)
                        // 아무 작업도 하지 않음 (pass through)
                    } else {
                        // 4. 일반 충돌 (사망)
                        triggerGameOver()
                    }
                }
            }
        }

        // 공중 적(드론)과의 충돌 판정
        for (drone in platformManager.getFlyingEnemies()) {
            if (drone.isAlive && player.collidesWith(drone)) {
                val droneBB = drone.getBoundingBox()
                
                if (player.isDashing || player.isGiant) {
                    drone.die()
                    particleSystem.spawnExplosion(
                        drone.x, drone.y,
                        intArrayOf(Color.rgb(255, 110, 70), Color.rgb(120, 80, 200)),
                        25
                    )
                    triggerShake(0.18f, 22f)
                } else {
                    val overlapX = minOf(playerBB.right, droneBB.right) - maxOf(playerBB.left, droneBB.left)
                    val overlapY = minOf(playerBB.bottom, droneBB.bottom) - maxOf(playerBB.top, droneBB.top)
                    
                    val prevPlayerBottom = playerBB.bottom - player.currentVelocityY * dt
                    val prevDroneTop = droneBB.top - drone.currentVelocityY * dt
                    
                    val wasAbove = prevPlayerBottom <= prevDroneTop + 15f
                    val isFallingRelative = (player.currentVelocityY - drone.currentVelocityY) > -100f
                    
                    if (isFallingRelative && (wasAbove || (overlapY < overlapX * 1.5f && playerBB.bottom <= droneBB.centerY()))) {
                        player.bounce()
                        SoundEffects.playStomp()
                        particleSystem.spawnExplosion(
                            drone.x, droneBB.top,
                            intArrayOf(Color.rgb(255, 225, 95), Color.rgb(120, 80, 200)),
                            15
                        )
                        triggerShake(0.12f, 10f)
                    } else if (player.isInvincible || player.isReturning) {
                        // 복귀 중이거나 대시 직후 무적 상태일 때는 패스스루
                    } else {
                        triggerGameOver()
                    }
                }
            }
        }

        // 가시 장애물과의 충돌 판정
        for (spike in platformManager.getSpikes()) {
            if (spike.isAlive && player.collidesWith(spike)) {
                if (player.isGiant || player.isFever) {
                    spike.die()
                    SoundEffects.playStomp()
                    particleSystem.spawnExplosion(
                        spike.x, spike.y,
                        intArrayOf(Color.rgb(255, 60, 60), Color.rgb(110, 120, 130)),
                        15
                    )
                    triggerShake(0.12f, 10f)
                } else {
                    // 일반 대시 상태 포함 일반 상태에서는 통과하지 못하고 충돌 사망 (점프 회피 강제)
                    triggerGameOver()
                }
            }
        }

        // 게임 오버 체크: 플레이어가 화면 아래로 추락
        if (player.screenY > 1600f) {
            triggerGameOver()
        }

        val attemptedPlayerX = player.screenX
        val returnDistance = (beforePlayerX - attemptedPlayerX).coerceAtLeast(0f)
        val overflowDistance = player.clampForwardLimit(SCROLL_TRIGGER_X)
        // 피버 모드일 때는 대시 여부에 무관하게 최소 스크롤이 자동으로 계속 진행되어 가속 질주를 연출함
        val feverAutoScroll = if (player.isFever) 1400f * gctx.frameTime else 0f
        pendingScrollDistance += returnDistance + overflowDistance + feverAutoScroll

        val scrollStep = nextScrollStep(gctx.frameTime)
        if (scrollStep > 0f) {
            background.scrollBy(scrollStep * BG_SCROLL_RATIO)
            platformManager.scrollBy(scrollStep)
            particleSystem.scrollBy(scrollStep)
            totalDistance += scrollStep
        }

        if (player.isDashing) {
            // 대시 중일 때 스트레치를 목표치(MAX_TRAIL_STRETCH)까지 점진적으로 증가
            activeTrailStretch = approach(activeTrailStretch, MAX_TRAIL_STRETCH, TRAIL_STRETCH_SPEED * gctx.frameTime)
        } else {
            // 대시가 끝나면 스트레치를 서서히 줄여서 꼬리가 머리쪽으로 따라오게 함
            activeTrailStretch = approach(activeTrailStretch, 0f, TRAIL_STRETCH_SPEED * 1.5f * gctx.frameTime)
        }

        // Head는 항상 플레이어 위치
        val trailHeadX = player.screenX
        // Tail은 플레이어 위치에서 activeTrailStretch만큼 뒤쪽(왼쪽)
        val trailTailX = trailHeadX - activeTrailStretch
        
        dashTrail.setHead(trailHeadX, player.screenY)
        dashTrail.setTail(trailTailX, player.screenY)

        if (wasDashing && !player.isDashing) {
            // 끝날 때의 Head 위치 고정
            dashTrail.finish(trailHeadX, player.screenY)
        }
    }

    private fun approach(current: Float, target: Float, amount: Float): Float {
        return if (current < target) {
            minOf(target, current + amount)
        } else {
            maxOf(target, current - amount)
        }
    }

    private fun nextScrollStep(dt: Float): Float {
        if (pendingScrollDistance <= 0f) return 0f

        val easedStep = pendingScrollDistance * SCROLL_EASE * dt
        val minStep = SCROLL_MIN_SPEED * dt
        val maxStep = SCROLL_MAX_SPEED * dt
        val scrollStep = minOf(maxOf(easedStep, minStep), maxStep, pendingScrollDistance)

        pendingScrollDistance -= scrollStep
        return scrollStep
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        if (shakeTimeLeft > 0f) {
            val dx = (Math.random().toFloat() * 2f - 1f) * shakeIntensity
            val dy = (Math.random().toFloat() * 2f - 1f) * shakeIntensity
            canvas.translate(dx, dy)
        }
        world.draw(canvas)
        canvas.restore()

        val distanceInMeters = (totalDistance / 100f).toInt()
        canvas.drawText("${distanceInMeters}m", gctx.metrics.width / 2f, 150f, scorePaint)

        

        // 피버 타임 화면 테두리 네온 및 안내 텍스트 연출
        if (player.isFever) {
            val feverBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(255, 0, 128)
                style = Paint.Style.STROKE
                strokeWidth = 24f
            }
            val pulse = Math.abs(Math.sin(System.currentTimeMillis() * 0.01)).toFloat()
            feverBorderPaint.alpha = (110 + 110 * pulse).toInt().coerceIn(0, 255)
            
            val borderRect = RectF(0f, 0f, gctx.metrics.width, gctx.metrics.height)
            canvas.drawRect(borderRect, feverBorderPaint)
            
            val feverTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(255, 0, 128)
                textAlign = Paint.Align.CENTER
                textSize = 54f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                setShadowLayer(10f, 0f, 0f, Color.WHITE)
            }
            canvas.drawText("FEVER TIME!!", gctx.metrics.width / 2f, 220f, feverTextPaint)
        }

        canvas.drawText("Dash Hero", gctx.metrics.width / 2f, 360f, titlePaint)
        
        if (state == State.GAME_OVER) {
            canvas.drawColor(Color.argb(160, 0, 0, 0))
            canvas.drawText("GAME OVER", gctx.metrics.width / 2f, gctx.metrics.height / 2f - 50f, titlePaint)
            
            val bestScore = HighScoreManager.getHighScore()
            canvas.drawText("Score: ${distanceInMeters}m  (Best: ${bestScore}m)", gctx.metrics.width / 2f, gctx.metrics.height / 2f + 50f, gameOverScorePaint)
            canvas.drawText("Tap to Restart", gctx.metrics.width / 2f, gctx.metrics.height / 2f + 180f, bodyPaint)
        } else {
            // 피버 배터리 게이지 UI 렌더링 (화면 좌측 상단)
            val startUiX = 80f
            val uiY = 110f
            val spacing = 64f
            val currentFeverBatteries = player.feverBatteryCount
            
            val feverBatteryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(0, 220, 255) // Cyan glowing battery
                style = Paint.Style.FILL
                setShadowLayer(8f, 0f, 0f, Color.rgb(0, 220, 255))
            }
            val feverEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(60, 255, 255, 255)
                style = Paint.Style.FILL
            }
            
            val rectWidth = 40f
            val rectHeight = 24f
            for (i in 0 until player.maxFeverBatteries) {
                val rx = startUiX + i * spacing
                val rect = RectF(rx - rectWidth/2f, uiY - rectHeight/2f, rx + rectWidth/2f, uiY + rectHeight/2f)
                if (i < currentFeverBatteries) {
                    canvas.drawRoundRect(rect, 6f, 6f, feverBatteryPaint)
                    // Draw a small cap on the right
                    canvas.drawRect(rx + rectWidth/2f, uiY - 5f, rx + rectWidth/2f + 4f, uiY + 5f, feverBatteryPaint)
                } else {
                    canvas.drawRoundRect(rect, 6f, 6f, feverEmptyPaint)
                    canvas.drawRect(rx + rectWidth/2f, uiY - 5f, rx + rectWidth/2f + 4f, uiY + 5f, feverEmptyPaint)
                }
            }

            // 버프 타이머 UI 렌더링 (피버 배터리 게이지 오른쪽)
            var buffOffset = 0f
            val buffUiY = 110f
            val buffStartX = 420f
            val barHeight = 16f
            val barMaxWidth = 100f
            
            if (player.isMagnetActive) {
                val magnetPaint = Paint().apply { color = Color.rgb(60, 150, 255); style = Paint.Style.FILL }
                val ratio = player.magnetTimeLeft / 6.0f
                canvas.drawRect(buffStartX, buffUiY - barHeight / 2f, buffStartX + barMaxWidth * ratio, buffUiY + barHeight / 2f, magnetPaint)
                buffOffset += 120f
            }
            
            if (player.isGiant) {
                val starBarPaint = Paint().apply { color = Color.rgb(255, 170, 0); style = Paint.Style.FILL }
                val ratio = player.giantTimeLeft / 5.0f
                val sx = buffStartX + buffOffset
                canvas.drawRect(sx, buffUiY - barHeight / 2f, sx + barMaxWidth * ratio, buffUiY + barHeight / 2f, starBarPaint)
            }
            
            // Draw Pause button in upper right
            val pw = gctx.metrics.width
            pauseBtnRect.set(pw - 120f, 60f, pw - 40f, 140f)
            canvas.drawRoundRect(pauseBtnRect, 15f, 15f, pauseBtnPaint)
            
            // Draw pause bars (||)
            canvas.drawRect(pw - 95f, 80f, pw - 85f, 120f, pauseSymbolPaint)
            canvas.drawRect(pw - 75f, 80f, pw - 65f, 120f, pauseSymbolPaint)
        }
    }

    private fun triggerGameOver() {
        if (state == State.GAME_OVER) return
        state = State.GAME_OVER
        SoundEffects.playGameOver()

        // Save high score
        val distanceInMeters = (totalDistance / 100f).toInt()
        HighScoreManager.updateHighScore(distanceInMeters)
    }

    private fun triggerShake(duration: Float, intensity: Float) {
        shakeTimeLeft = duration
        shakeIntensity = intensity
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (state == State.GAME_OVER) {
                gctx.sceneStack.change(MainScene(gctx))
                return true
            }

            // Convert to game coordinates
            val gameCoord = gctx.metrics.fromScreen(event.x, event.y)
            val gx = gameCoord.x
            val gy = gameCoord.y

            if (state == State.RUNNING) {
                val pw = gctx.metrics.width
                pauseBtnRect.set(pw - 120f, 60f, pw - 40f, 140f)
                if (pauseBtnRect.contains(gx, gy)) {
                    gctx.sceneStack.push(PauseScene(gctx))
                    return true
                }
            }

            // 대시 시작 시점에 스트레치를 0에서 시작하여 '개기지' 않게 함
            if (!player.isDashing) {
                activeTrailStretch = 0f
            }
            
            // 꼬리 위치를 플레이어 현재 위치에서 시작 (0에서부터 뻗어나감)
            dashTrail.start(player.screenX - activeTrailStretch, player.screenY)
            player.dash()
            return true
        }
        return false
    }

    companion object {
        private const val SCROLL_TRIGGER_X = 620f
        private const val BG_SCROLL_RATIO = 0.7f
        private const val BASE_BG_SPEED = 0f
        private const val SCROLL_EASE = 12f
        private const val SCROLL_MIN_SPEED = 180f
        private const val SCROLL_MAX_SPEED = 2400f
        private const val MAX_TRAIL_STRETCH = 300f
        private const val TRAIL_STRETCH_SPEED = 1800f
    }
}
