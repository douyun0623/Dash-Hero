package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
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

    private val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(80, 180, 120)
    }

    private val playerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 205, 80)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(18, 24, 36))
        canvas.drawText("Dash Hero", gctx.metrics.width / 2f, 360f, titlePaint)
        canvas.drawText("MainScene is running with a2dg", gctx.metrics.width / 2f, 430f, bodyPaint)

        canvas.drawRoundRect(RectF(130f, 1180f, 770f, 1240f), 24f, 24f, groundPaint)
        canvas.drawRoundRect(RectF(380f, 1040f, 520f, 1180f), 32f, 32f, playerPaint)
    }
}
