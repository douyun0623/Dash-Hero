package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class GroundPlatform(
    private var x: Float,
    private val y: Float,
    private val width: Float,
    private val height: Float,
) : IGameObject {
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(80, 180, 120)
    }
    var scrollSpeed = 0f

    fun scrollBy(distance: Float, screenWidth: Float) {
        x -= distance
        recycleIfNeeded(screenWidth)
    }

    override fun update(gctx: GameContext) {
        x += scrollSpeed * gctx.frameTime
        recycleIfNeeded(gctx.metrics.width)
    }

    override fun draw(canvas: Canvas) {
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 24f, 24f, paint)
    }

    private fun recycleIfNeeded(screenWidth: Float) {
        if (x + width / 2f < 0f) {
            x += screenWidth + width
        }
    }
}
