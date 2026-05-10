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
    private val dashDuration = 0.24f
    private val baseX = x
    private val dashLeadX = 310f
    private val returnSpeed = 1500f
    private val crouchDuration = 0.16f

    private var velocityY = 0f
    private var crouchTimeLeft = crouchDuration
    private var dashTimeLeft = 0f
    private var dashLockedY = y
    val isDashing: Boolean
        get() = dashTimeLeft > 0f

    fun dash() {
        dashTimeLeft = dashDuration
        dashLockedY = y
        velocityY = 0f
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime

        if (isDashing) {
            y = dashLockedY
            dashTimeLeft -= dt
            if (dashTimeLeft <= 0f) {
                dashTimeLeft = 0f
                velocityY = 0f
            }
        } else if (crouchTimeLeft > 0f) {
            y = groundY
            velocityY = 0f
            crouchTimeLeft -= dt
            if (crouchTimeLeft <= 0f) {
                crouchTimeLeft = 0f
                velocityY = jumpVelocity
            }
        } else {
            velocityY += gravity * dt
            y += velocityY * dt

            if (y >= groundY) {
                y = groundY
                velocityY = 0f
                crouchTimeLeft = crouchDuration
            }
        }

        val targetX = if (isDashing) baseX + dashLeadX else baseX
        if (x < targetX) {
            x = minOf(targetX, x + returnSpeed * dt)
        } else if (x > targetX) {
            x = maxOf(targetX, x - returnSpeed * dt)
        }
    }

    override fun draw(canvas: Canvas) {
        val isCrouching = !isDashing && crouchTimeLeft > 0f
        paint.color = if (isDashing) {
            Color.rgb(255, 110, 70)
        } else if (isCrouching) {
            Color.rgb(255, 225, 95)
        } else {
            Color.rgb(255, 205, 80)
        }

        val drawWidth = if (isCrouching) width * 1.18f else width
        val drawHeight = if (isCrouching) height * 0.76f else height
        bounds.set(x - drawWidth / 2f, y - drawHeight / 2f, x + drawWidth / 2f, y + drawHeight / 2f)
        canvas.drawRoundRect(bounds, 32f, 32f, paint)
    }
}
