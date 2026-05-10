package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Player(
    private var x: Float,
    private var y: Float,
) : IGameObject {
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 205, 80)
    }

    private val width = 140f
    private val height = 140f
    private var elapsedTime = 0f

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        val bobY = y + kotlin.math.sin(elapsedTime * 4f) * 8f
        bounds.set(x - width / 2f, bobY - height / 2f, x + width / 2f, bobY + height / 2f)
        canvas.drawRoundRect(bounds, 32f, 32f, paint)
    }
}
