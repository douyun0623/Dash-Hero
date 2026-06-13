package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

enum class PlatformType {
    NORMAL,
    MOVING_X,
    MOVING_Y
}

class GroundPlatform(
    var x: Float,
    var y: Float,
    val width: Float,
    private val height: Float,
    var type: PlatformType = PlatformType.NORMAL,
) : IGameObject {
    var baseX = x
    var baseY = y
    private var time = 0f

    val screenX: Float
        get() = x
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var scrollSpeed = 0f

    fun scrollBy(distance: Float) {
        x -= distance
        baseX -= distance
    }

    override fun update(gctx: GameContext) {
        time += gctx.frameTime
        
        if (scrollSpeed != 0f) {
            val dist = scrollSpeed * gctx.frameTime
            x += dist
            baseX += dist
        }

        when (type) {
            PlatformType.MOVING_X -> {
                val range = 120f
                val speed = 2.0f
                x = baseX + Math.sin(time.toDouble() * speed).toFloat() * range
            }
            PlatformType.MOVING_Y -> {
                val range = 100f
                val speed = 1.8f
                y = baseY + Math.sin(time.toDouble() * speed).toFloat() * range
            }
            else -> {
                x = baseX
                y = baseY
            }
        }
    }

    override fun draw(canvas: Canvas) {
        paint.color = when (type) {
            PlatformType.NORMAL -> Color.rgb(80, 180, 120)
            PlatformType.MOVING_X, PlatformType.MOVING_Y -> Color.rgb(70, 160, 220)
        }

        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 24f, 24f, paint)
    }

    val topY: Float
        get() = y - height / 2f

    fun isOffScreen(): Boolean {
        return x + width / 2f < -100f
    }
}
