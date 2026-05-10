package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.dashhero.R
import com.example.dashhero.game.objects.DashScrollBackground
import com.example.dashhero.game.objects.GroundPlatform
import com.example.dashhero.game.objects.Player
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BG,
        PLATFORM,
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
    private var scrollSpeed = BASE_SCROLL_SPEED

    init {
        world.add(background, Layer.BG)
        world.add(platform, Layer.PLATFORM)
        world.add(player, Layer.PLAYER)
    }

    override fun update(gctx: GameContext) {
        val targetScrollSpeed = if (player.isDashing) DASH_SCROLL_SPEED else BASE_SCROLL_SPEED
        scrollSpeed += (targetScrollSpeed - scrollSpeed) * SCROLL_ACCELERATION

        background.speed = scrollSpeed * BG_SPEED_RATIO
        platform.scrollSpeed = scrollSpeed
        super.update(gctx)
    }

    override fun draw(canvas: Canvas) {
        world.draw(canvas)

        canvas.drawText("Dash Hero", gctx.metrics.width / 2f, 360f, titlePaint)
        canvas.drawText("MainScene is running with a2dg", gctx.metrics.width / 2f, 430f, bodyPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            player.dash()
            return true
        }
        return false
    }

    companion object {
        private const val BASE_SCROLL_SPEED = -80f
        private const val DASH_SCROLL_SPEED = -760f
        private const val BG_SPEED_RATIO = 0.7f
        private const val BASE_BG_SPEED = BASE_SCROLL_SPEED * BG_SPEED_RATIO
        private const val SCROLL_ACCELERATION = 0.18f
    }
}
