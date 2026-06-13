package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class SpikyEnemy(
    var x: Float,
    var y: Float,
    var parentPlatform: GroundPlatform? = null
) : IGameObject, IBoxCollidable {
    enum class State {
        ALIVE, DEAD
    }

    private val bounds = RectF()
    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(100, 110, 120) // 강철 회색 몸체
    }
    private val spikePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 75, 50) // 선명한 다홍색 가시
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.rgb(255, 75, 50))
    }

    private val width = 110f
    private val height = 110f
    private val gravity = 2400f

    private var velocityX = -120f
    private var velocityY = 0f
    private var state = State.ALIVE
    private var rotation = 0f
    private var rotationSpeed = 0f

    private var relativeX = if (parentPlatform != null) x - parentPlatform!!.x else 0f

    val isAlive: Boolean get() = state == State.ALIVE
    val currentVelocityY: Float get() = velocityY

    fun die() {
        if (state == State.DEAD) return
        state = State.DEAD
        parentPlatform = null
        velocityX = 1200f // 오른쪽으로 날아감
        velocityY = -1000f // 위로 튀어오름
        rotationSpeed = 720f // 초당 2회전
    }

    fun scrollBy(distance: Float) {
        if (state == State.ALIVE) {
            if (parentPlatform == null) {
                x -= distance
            }
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

        if (parentPlatform != null && parentPlatform!!.isFell) {
            parentPlatform = null
        }

        if (parentPlatform != null) {
            relativeX += velocityX * dt
            val halfW = parentPlatform!!.width / 2f
            if (relativeX < -halfW + width / 2f) {
                relativeX = -halfW + width / 2f
                velocityX = -velocityX
            } else if (relativeX > halfW - width / 2f) {
                relativeX = halfW - width / 2f
                velocityX = -velocityX
            }
            x = parentPlatform!!.x + relativeX
        } else {
            x += velocityX * dt
        }

        val currentPlatform = parentPlatform ?: platformManager.getPlatformAt(x)
        val platformTopY = currentPlatform?.topY ?: Float.MAX_VALUE

        if (platformTopY == Float.MAX_VALUE) {
            velocityY += gravity * dt
            y += velocityY * dt
        } else {
            y = platformTopY - height / 2f
            velocityY = 0f
        }
    }

    override fun update(gctx: GameContext) {
        // MainScene에서 updateWithCollision을 직접 호출함
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(rotation, x, y)
        
        // 몸체 그리기 (둥근 사각형)
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 20f, 20f, bodyPaint)
        
        // 가시 그리기
        if (state == State.ALIVE) {
            val spikePath = Path()
            val leftEdge = x - width / 2f
            val spikeLength = 30f
            val spikeWidth = 26f
            
            val yOffsets = listOf(-height / 3f, 0f, height / 3f)
            for (offsetY in yOffsets) {
                val centerY = y + offsetY
                spikePath.reset()
                spikePath.moveTo(leftEdge - spikeLength, centerY)
                spikePath.lineTo(leftEdge, centerY - spikeWidth / 2f)
                spikePath.lineTo(leftEdge, centerY + spikeWidth / 2f)
                spikePath.close()
                canvas.drawPath(spikePath, spikePaint)
            }
        }
        
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
        return x < -200f || y > 2000f || x > 3000f
    }

    fun getBoundingBox(): RectF {
        // 가시 길이만큼 충돌 영역을 약간 확장 (왼쪽으로 조금 더 넓힘)
        val extendLeft = if (state == State.ALIVE) 15f else 0f
        bounds.set(x - width / 2f - extendLeft, y - height / 2f, x + width / 2f, y + height / 2f)
        return bounds
    }

    override val collisionRect: RectF
        get() = getBoundingBox()
}
