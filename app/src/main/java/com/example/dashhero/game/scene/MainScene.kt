package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.dashhero.game.objects.GroundPlatform
import com.example.dashhero.game.objects.Player
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
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

    init {
        world.add(GroundPlatform(450f, 1210f, 640f, 60f), Layer.PLATFORM)
        world.add(player, Layer.PLAYER)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(18, 24, 36))
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
}
