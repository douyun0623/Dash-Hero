package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin

class DroneEnemy(
    var x: Float,
    var y: Float,
) : IGameObject, IBoxCollidable {
    enum class State {
        ALIVE, DEAD
    }

    private val bounds = RectF()
    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 60, 60) // Red robotic drone
    }

    private val metalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(110, 115, 125)
    }

    private val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        setShadowLayer(10f, 0f, 0f, Color.YELLOW)
    }

    private val width = 110f
    private val height = 70f
    private val gravity = 2400f

    private var baseLockedX = x
    private var baseLockedY = y
    private var timeAccum = (Math.random() * 5.0).toFloat() // Random phase start

    private var velocityX = 0f
    private var velocityY = 0f
    private var state = State.ALIVE
    private var rotation = 0f
    private var rotationSpeed = 0f

    val isAlive: Boolean get() = state == State.ALIVE
    val currentVelocityY: Float 
        get() = if (state == State.DEAD) velocityY else 4.0f * cos(timeAccum * 4.0f) * 25f

    fun die() {
        if (state == State.DEAD) return
        state = State.DEAD
        velocityX = 1300f // Fly rightwards
        velocityY = -900f // Pop up
        rotationSpeed = 800f // Spin fast
    }

    fun scrollBy(distance: Float) {
        if (state == State.ALIVE) {
            baseLockedX -= distance
        } else {
            x -= distance
        }
    }

    fun isOffScreen(): Boolean {
        return x < -200f || y > 2000f || x > 3000f
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime

        if (state == State.DEAD) {
            velocityY += gravity * dt
            x += velocityX * dt
            y += velocityY * dt
            rotation += rotationSpeed * dt
            return
        }

        // Hover & Patrol animation
        timeAccum += dt
        baseLockedY += sin(timeAccum * 4.0f) * 25f * dt
        baseLockedX += cos(timeAccum * 2.0f) * 75f * dt

        x = baseLockedX
        y = baseLockedY
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(rotation, x, y)

        // 1. Draw side wings/props
        canvas.drawRect(x - 70f, y - 8f, x - 40f, y + 8f, metalPaint)
        canvas.drawRect(x + 40f, y - 8f, x + 70f, y + 8f, metalPaint)
        canvas.drawCircle(x - 55f, y - 8f, 18f, metalPaint)
        canvas.drawCircle(x + 55f, y - 8f, 18f, metalPaint)

        // 2. Draw main body
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 20f, 20f, bodyPaint)

        // 3. Draw glowing yellow eye in the center
        canvas.drawCircle(x, y - 5f, 12f, eyePaint)

        canvas.restore()

    }

    fun getBoundingBox(): RectF {
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        return bounds
    }

    override val collisionRect: RectF
        get() = getBoundingBox()
}
