package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.dashhero.game.objects.PlatformManager
import com.example.dashhero.game.sound.SoundEffects
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Player(
    private var x: Float,
    private var y: Float,
) : IGameObject, IBoxCollidable {
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 205, 80)
    }

    private val width = 140f
    private val height = 140f

    private val gravity = 2400f
    private val jumpVelocity = -1000f
    private val dashDuration = 0.24f
    private val baseX = x
    private val dashLeadX = 620f
    private val dashMoveSpeed = 1500f
    private val returnEase = 8.5f
    private val returnMinSpeed = 120f
    private val returnSnapDistance = 1.5f
    private val crouchDuration = 0.16f

    private var velocityY = 0f
    private var crouchTimeLeft = crouchDuration
    private var dashTimeLeft = 0f
    private var dashLockedY = y
    val isDashing: Boolean
        get() = dashTimeLeft > 0f
    val isReturning: Boolean
        get() = !isDashing && x > baseX + 2f
    val dashForwardRatio: Float
        get() = ((x - baseX) / dashLeadX).coerceIn(0f, 1f)
    val screenX: Float
        get() = x
    val screenY: Float
        get() = y
    val currentVelocityY: Float
        get() = velocityY

    fun dash() {
        dashTimeLeft = dashDuration
        dashLockedY = y
        velocityY = 0f
        SoundEffects.playDash()
    }

    fun bounce() {
        velocityY = jumpVelocity * 0.9f // 점프보다 약간 낮은 높이로 튕김
        crouchTimeLeft = 0f // 즉시 공중 상태로
    }

    fun clampForwardLimit(limitX: Float): Float {
        val overflow = (x - limitX).coerceAtLeast(0f)
        if (overflow > 0f) {
            x = limitX
        }
        return overflow
    }

    override fun update(gctx: GameContext) {
        // 이 메서드는 인터페이스 구현을 위해 남겨두고, 실제 로직은 아래 오버로드된 update에서 수행하거나 
        // MainScene에서 캐스팅하여 호출해야 합니다.
        // 하지만 프레임워크의 world.update()가 인자 없는 update를 호출하므로, 
        // 외부에서 platformManager를 전달받을 수 있도록 구조 변경이 필요할 수 있습니다.
    }

    fun updateWithCollision(gctx: GameContext, platformManager: PlatformManager) {
        val dt = gctx.frameTime

        // 현재 위치에서의 발판 확인
        val currentPlatform = platformManager.getPlatformAt(x)
        val platformTopY = currentPlatform?.topY ?: Float.MAX_VALUE

        if (isDashing) {
            y = dashLockedY
            dashTimeLeft -= dt
            if (dashTimeLeft <= 0f) {
                dashTimeLeft = 0f
                velocityY = 0f
            }
        } else if (crouchTimeLeft > 0f) {
            // 발판 위에 있을 때만 웅크리기 가능 (오차 범위를 넉넉하게 줌)
            if (y >= platformTopY - height / 2f - 20f) {
                y = platformTopY - height / 2f
                velocityY = 0f
                crouchTimeLeft -= dt
                if (crouchTimeLeft <= 0f) {
                    crouchTimeLeft = 0f
                    velocityY = jumpVelocity
                    SoundEffects.playJump()
                }
            } else {
                // 발판이 없으면 웅크리기 무시하고 추락
                crouchTimeLeft = 0f
                velocityY += gravity * dt
                y += velocityY * dt
            }
        } else {
            velocityY += gravity * dt
            val nextY = y + velocityY * dt

            // 착지 판정: 위에서 아래로 떨어질 때 발판 상단면을 지나치면 착지
            // 발판 위 10px 정도의 여유를 두어 프레임 드랍 시에도 착지 판정이 잘 되도록 함
            val landingY = platformTopY - height / 2f
            if (velocityY >= 0 && y <= landingY + 10f && nextY >= landingY) {
                y = landingY
                velocityY = 0f
                crouchTimeLeft = crouchDuration
            } else {
                y = nextY
            }
        }

        val targetX = if (isDashing) baseX + dashLeadX else baseX
        if (x < targetX) {
            x = minOf(targetX, x + dashMoveSpeed * dt)
        } else if (x > targetX) {
            val distance = x - targetX
            if (distance <= returnSnapDistance) {
                x = targetX
            } else {
                val easedStep = maxOf(distance * returnEase * dt, returnMinSpeed * dt)
                x = maxOf(targetX, x - easedStep)
            }
        }
    }

    fun getBoundingBox(): RectF {
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        return bounds
    }

    override val collisionRect: RectF
        get() = getBoundingBox()

    override fun draw(canvas: Canvas) {
        val isCrouching = !isDashing && crouchTimeLeft > 0f
        paint.color = if (isDashing) {
            Color.rgb(255, 110, 70)
        } else if (isCrouching) {
            Color.rgb(255, 225, 95)
        } else if (isReturning) {
            Color.argb(160, 255, 205, 80) // 복귀 중일 때는 반투명한 노란색
        } else {
            Color.rgb(255, 205, 80)
        }

        val drawWidth = if (isCrouching) width * 1.18f else width
        val drawHeight = if (isCrouching) height * 0.76f else height
        bounds.set(x - drawWidth / 2f, y - drawHeight / 2f, x + drawWidth / 2f, y + drawHeight / 2f)
        canvas.drawRoundRect(bounds, 32f, 32f, paint)

        // 디버그용 충돌 박스 가시화 (빨간색 테두리)
        val debugPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val bb = getBoundingBox()
        canvas.drawRect(bb, debugPaint)
    }
}
