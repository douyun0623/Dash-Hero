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

        val afterPlayerX = player.screenX
        val returnDistance = (beforePlayerX - afterPlayerX).coerceAtLeast(0f)
        val overflowDistance = player.clampForwardLimit(SCROLL_TRIGGER_X)
        pendingScrollDistance += returnDistance + overflowDistance

        val scrollStep = nextScrollStep(gctx.frameTime)
        if (scrollStep > 0f) {
            background.scrollBy(scrollStep * BG_SCROLL_RATIO)
            platform.scrollBy(scrollStep, gctx.metrics.width)
        }

        dashTrail.setHead(player.screenX, player.screenY)
        if (wasDashing && !player.isDashing) {
            dashTrail.finish(player.screenX, player.screenY)
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
            dashTrail.start(player.screenX, player.screenY)
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
    }
}
