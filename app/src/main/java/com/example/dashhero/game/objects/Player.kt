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

    private val groundY = 1110f
    private val gravity = 2400f
    private val jumpVelocity = -980f
    private val dashSpeed = 1150f
    private val dashDuration = 0.32f
    private val dashCooldown = 0.18f

    private var velocityY = jumpVelocity
    private var dashTimeLeft = 0f
    private var dashCooldownLeft = 0f

    fun dash() {
        if (dashCooldownLeft > 0f) return
        dashTimeLeft = dashDuration
        dashCooldownLeft = dashDuration + dashCooldown
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime

        velocityY += gravity * dt
        y += velocityY * dt

        if (y >= groundY) {
            y = groundY
            velocityY = jumpVelocity
        }

        if (dashTimeLeft > 0f) {
            x += dashSpeed * dt
            dashTimeLeft -= dt
        }
        if (dashCooldownLeft > 0f) {
            dashCooldownLeft -= dt
        }

        val halfWidth = width / 2f
        x = x.coerceIn(halfWidth, gctx.metrics.width - halfWidth)
    }

    override fun draw(canvas: Canvas) {
        paint.color = if (dashTimeLeft > 0f) {
            Color.rgb(255, 110, 70)
        } else {
            Color.rgb(255, 205, 80)
        }
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 32f, 32f, paint)
    }
}
