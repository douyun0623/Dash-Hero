package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Battery(
    var x: Float,
    var y: Float,
) : IGameObject, IBoxCollidable {
    private val width = 50f
    private val height = 75f
    private val bounds = RectF()
    var isAlive = true
        private set

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(0, 220, 255) // Cyan battery body
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.rgb(0, 220, 255))
    }

    private val capPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(220, 220, 220)
        style = Paint.Style.FILL
    }

    private val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private val debugPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun collect() {
        isAlive = false
    }

    fun scrollBy(distance: Float) {
        x -= distance
    }

    fun isOffScreen(): Boolean {
        return x < -200f
    }

    override fun update(gctx: GameContext) {
        // 배터리는 고정되어 있으나 필요한 경우 둥둥 뜨는 애니메이션 효과를 줄 수 있음
        y += Math.sin(System.currentTimeMillis() * 0.007).toFloat() * 0.4f
    }

    override fun draw(canvas: Canvas) {
        if (!isAlive) return

        // 1. 배터리 본체 그리기 (둥근 사각형)
        bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
        canvas.drawRoundRect(bounds, 12f, 12f, bodyPaint)

        // 2. 배터리 상단 단자 그리기
        canvas.drawRect(x - 10f, y - height / 2f - 8f, x + 10f, y - height / 2f, capPaint)

        // 3. 중앙에 번개 모양 데코레이션 그리기
        val boltPath = Path().apply {
            moveTo(x - 5f, y - 20f)
            lineTo(x + 12f, y - 5f)
            lineTo(x + 3f, y)
            lineTo(x + 10f, y + 20f)
            lineTo(x - 12f, y + 5f)
            lineTo(x - 3f, y)
            close()
        }
        canvas.drawPath(boltPath, boltPaint)
    }

    override val collisionRect: RectF
        get() {
            bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
            return bounds
        }
}
