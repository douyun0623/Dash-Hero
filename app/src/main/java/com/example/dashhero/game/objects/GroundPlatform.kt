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
    MOVING_Y,
    CRUMBLING
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
    
    var isCrumbling = false
    var crumbleTimer = 0.8f
    var isFell = false

    val screenX: Float
        get() = x
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var scrollSpeed = 0f

    fun scrollBy(distance: Float) {
        x -= distance
        baseX -= distance
    }

    fun stepOn() {
        if (type == PlatformType.CRUMBLING && !isCrumbling) {
            isCrumbling = true
        }
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
            PlatformType.CRUMBLING -> {
                if (isCrumbling) {
                    crumbleTimer -= gctx.frameTime
                    if (crumbleTimer <= 0f) {
                        isFell = true
                        y += 1000f * gctx.frameTime
                    } else {
                        val shake = Math.sin(time.toDouble() * 60.0).toFloat() * 4f
                        x = baseX + shake
                    }
                }
            }
            else -> {
                x = baseX
                y = baseY
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (isFell) return

        paint.color = when (type) {
            PlatformType.NORMAL -> Color.rgb(80, 180, 120)
            PlatformType.MOVING_X, PlatformType.MOVING_Y -> Color.rgb(70, 160, 220)
            PlatformType.CRUMBLING -> {
                if (isCrumbling) {
                    if ((time * 10).toInt() % 2 == 0) {
                        Color.rgb(220, 70, 70)
                    } else {
                        Color.rgb(220, 120, 70)
                    }
                } else {
                    Color.rgb(220, 120, 70)
                }
            }
        }

        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 24f, 24f, paint)
    }

    val topY: Float
        get() = if (isFell) Float.MAX_VALUE else y - height / 2f

    fun isOffScreen(): Boolean {
        return x + width / 2f < -100f
    }
}
