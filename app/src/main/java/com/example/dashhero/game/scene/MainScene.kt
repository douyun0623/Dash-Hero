package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.dashhero.R
import com.example.dashhero.game.objects.DashScrollBackground
import com.example.dashhero.game.objects.DashTrail
import com.example.dashhero.game.objects.GroundPlatform
import com.example.dashhero.game.objects.Player
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

    override val world = World(enumValues<Layer>())

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

    private val player = Player(180f, 1110f)
    private val background = DashScrollBackground(gctx, R.drawable.bg_dash_city, BASE_BG_SPEED)
    private val platform = GroundPlatform(450f, 1210f, 640f, 60f)
    private val dashTrail = DashTrail()
    private var pendingScrollDistance = 0f
    private var activeTrailStretch = 0f

    init {
        world.add(background, Layer.BG)
        world.add(platform, Layer.PLATFORM)
        world.add(dashTrail, Layer.TRAIL)
        world.add(player, Layer.PLAYER)
    }

    override fun update(gctx: GameContext) {
        val beforePlayerX = player.screenX
        val wasDashing = player.isDashing
        background.speed = 0f
        platform.scrollSpeed = 0f
        super.update(gctx)

        val attemptedPlayerX = player.screenX
        val returnDistance = (beforePlayerX - attemptedPlayerX).coerceAtLeast(0f)
        val overflowDistance = player.clampForwardLimit(SCROLL_TRIGGER_X)
        pendingScrollDistance += returnDistance + overflowDistance

        val scrollStep = nextScrollStep(gctx.frameTime)
        if (scrollStep > 0f) {
            background.scrollBy(scrollStep * BG_SCROLL_RATIO)
            platform.scrollBy(scrollStep, gctx.metrics.width)
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
        world.draw(canvas)

        canvas.drawText("Dash Hero", gctx.metrics.width / 2f, 360f, titlePaint)
        canvas.drawText("MainScene is running with a2dg", gctx.metrics.width / 2f, 430f, bodyPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
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
