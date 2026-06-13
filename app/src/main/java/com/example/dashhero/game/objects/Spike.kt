package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Spike(
    var x: Float,
    var y: Float,
    var parentPlatform: GroundPlatform? = null
) : IGameObject, IBoxCollidable {
    enum class State {
        ALIVE, DEAD
    }

    private val width = 80f
    private val height = 60f
    private val bounds = RectF()
    private var state = State.ALIVE

    private val relativeX = if (parentPlatform != null) x - parentPlatform!!.x else 0f
    private val relativeY = if (parentPlatform != null) y - parentPlatform!!.y else 0f
    
    // For hit/death animation (spinning off-screen like enemies)
    private var velocityX = 0f
    private var velocityY = 0f
    private val gravity = 2400f
    private var rotation = 0f
    private var rotationSpeed = 0f

    val isAlive: Boolean get() = state == State.ALIVE

    fun die() {
        if (state == State.DEAD) return
        state = State.DEAD
        velocityX = 800f
        velocityY = -1000f
        rotationSpeed = 540f
    }

    fun scrollBy(distance: Float) {
        if (state == State.ALIVE) {
            if (parentPlatform == null) {
                x -= distance
            }
        }
    }

    fun isOffScreen(): Boolean {
        return x < -200f || y > 1600f
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime
        if (state == State.DEAD) {
            velocityY += gravity * dt
            x += velocityX * dt
            y += velocityY * dt
            rotation += rotationSpeed * dt
        } else if (parentPlatform != null) {
            if (parentPlatform!!.isFell) {
                parentPlatform = null
            } else {
                x = parentPlatform!!.x + relativeX
                y = parentPlatform!!.y + relativeY
            }
        }
    }

    private val spikePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(110, 120, 130) // Dark metallic silver
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.rgb(220, 60, 60)) // Red glow warning
    }

    private val tipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 60, 60) // Red dangerous tips
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        if (state == State.DEAD) {
            canvas.rotate(rotation, x, y)
        }

        // Draw 3 spikes side by side
        val startX = x - width / 2f
        val spikeWidth = width / 3f
        val topY = y - height / 2f
        val bottomY = y + height / 2f

        for (i in 0 until 3) {
            val sx = startX + i * spikeWidth
            val cx = sx + spikeWidth / 2f
            
            // Spike body
            val path = Path().apply {
                moveTo(sx, bottomY)
                lineTo(cx, topY)
                lineTo(sx + spikeWidth, bottomY)
                close()
            }
            canvas.drawPath(path, spikePaint)

            // Dangerous red tip
            val tipPath = Path().apply {
                moveTo(cx - spikeWidth / 4f, bottomY - height * 0.5f)
                lineTo(cx, topY)
                lineTo(cx + spikeWidth / 4f, bottomY - height * 0.5f)
                close()
            }
            canvas.drawPath(tipPath, tipPaint)
        }

        canvas.restore()
    }

    override val collisionRect: RectF
        get() {
            // slightly smaller collision box for fairness
            bounds.set(x - width * 0.35f, y - height * 0.35f, x + width * 0.35f, y + height * 0.5f)
            return bounds
        }
}
