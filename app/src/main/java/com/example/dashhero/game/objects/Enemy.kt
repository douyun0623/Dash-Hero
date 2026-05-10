package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin

class Enemy(
    var x: Float,
    var y: Float,
) : IGameObject {
    enum class State {
        ALIVE, DEAD
    }

    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(120, 80, 200) // 보라색 적
    }

    private val width = 110f
    private val height = 110f
    private val gravity = 2400f
    private val jumpVelocity = -800f // 플레이어(-1000f)보다 낮게 점프하여 밟기 쉽게 함
    private val crouchDuration = 0.2f

    private var velocityX = 0f
    private var velocityY = 0f
    private var crouchTimeLeft = crouchDuration
    private var state = State.ALIVE
    private var rotation = 0f
    private var rotationSpeed = 0f

    val isAlive: Boolean get() = state == State.ALIVE

    fun die() {
        if (state == State.DEAD) return
        state = State.DEAD
        velocityX = 1200f // 오른쪽으로 날아감
        velocityY = -1000f // 위로 튀어오름
        rotationSpeed = 720f // 초당 2회전
    }

    fun scrollBy(distance: Float) {
        if (state == State.ALIVE) {
            x -= distance
        }
    }

    fun updateWithCollision(gctx: GameContext, platformManager: PlatformManager) {
        val dt = gctx.frameTime

        if (state == State.DEAD) {
            velocityY += gravity * dt
            x += velocityX * dt
            y += velocityY * dt
            rotation += rotationSpeed * dt
            return
        }

        // ALIVE 상태: 발판 위에서 점프
        val currentPlatform = platformManager.getPlatformAt(x)
        val platformTopY = currentPlatform?.topY ?: Float.MAX_VALUE

        if (crouchTimeLeft > 0f) {
            if (y >= platformTopY - height / 2f - 20f) {
                y = platformTopY - height / 2f
                velocityY = 0f
                crouchTimeLeft -= dt
                if (crouchTimeLeft <= 0f) {
                    crouchTimeLeft = 0f
                    velocityY = jumpVelocity
                }
            } else {
                crouchTimeLeft = 0f
                velocityY += gravity * dt
                y += velocityY * dt
            }
        } else {
            velocityY += gravity * dt
            val nextY = y + velocityY * dt
            val landingY = platformTopY - height / 2f

            if (velocityY >= 0 && y <= landingY + 10f && nextY >= landingY) {
                y = landingY
                velocityY = 0f
                crouchTimeLeft = crouchDuration
            } else {
                y = nextY
            }
        }
    }

    override fun update(gctx: GameContext) {
        // MainScene에서 updateWithCollision을 호출함
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(rotation, x, y)
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 20f, 20f, paint)
        canvas.restore()

        // 디버그용 충돌 박스 가시화 (빨간색 테두리)
        val debugPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val bb = getBoundingBox()
        canvas.drawRect(bb, debugPaint)
    }

    fun isOffScreen(): Boolean {
        // 화면 왼쪽으로 나가거나, 죽어서 화면 밖으로 멀리 나갔을 때
        return x < -200f || y > 2000f || x > 3000f
    }
    
    fun getBoundingBox(): RectF {
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        return bounds
    }
}
