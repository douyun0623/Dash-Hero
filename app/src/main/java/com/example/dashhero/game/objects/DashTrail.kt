package com.example.dashhero.game.objects

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.hypot

class DashTrail : IGameObject {
    private enum class State {
        HIDDEN,
        DASHING,
        RETRACTING,
    }

    private val tail = PointF()
    private val head = PointF()
    private var retractStartLength = 0f
    private var state = State.HIDDEN
    private var alphaScale = 0f
    private var widthScale = 0f
    private var emissiveScale = 0f

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(70, 220, 255)
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        maskFilter = BlurMaskFilter(24f, BlurMaskFilter.Blur.NORMAL)
    }

    private val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(45, 170, 255)
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    fun start(x: Float, y: Float) {
        tail.set(x, y)
        head.set(x, y)
        alphaScale = 1f
        widthScale = 1f
        emissiveScale = 1f
        state = State.DASHING
    }

    fun setHead(x: Float, y: Float) {
        if (state != State.DASHING) return
        head.set(x, y)
    }

    fun finish(endX: Float, endY: Float) {
        if (state == State.DASHING) {
            head.set(endX, endY)
            retractStartLength = trailLength().coerceAtLeast(1f)
            state = State.RETRACTING
        }
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime

        when (state) {
            State.HIDDEN -> return
            State.DASHING -> {
                alphaScale = approach(alphaScale, 1f, TRAIL_APPEAR_SPEED * dt)
                widthScale = approach(widthScale, 1f, TRAIL_APPEAR_SPEED * dt)
                emissiveScale = approach(emissiveScale, 1f, TRAIL_APPEAR_SPEED * dt)
            }
            State.RETRACTING -> {
                val followRatio = (TRAIL_RETRACT_EASE * dt).coerceIn(0f, 1f)
                tail.x = lerp(tail.x, head.x, followRatio)
                tail.y = lerp(tail.y, head.y, followRatio)
                val lengthRatio = (trailLength() / retractStartLength).coerceIn(0f, 1f)
                alphaScale = lengthRatio
                widthScale = lengthRatio
                emissiveScale = lengthRatio

                if (trailLength() < MIN_TRAIL_LENGTH && alphaScale <= 0.02f) {
                    state = State.HIDDEN
                    alphaScale = 0f
                    widthScale = 0f
                    emissiveScale = 0f
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (state == State.HIDDEN || alphaScale <= 0f || trailLength() < 2f) return

        drawSegment(canvas, glowPaint, GLOW_WIDTH, 90, emissiveScale)
        drawSegment(canvas, ribbonPaint, RIBBON_WIDTH, 170, alphaScale)
        drawSegment(canvas, corePaint, CORE_WIDTH, 210, alphaScale)
    }

    private fun drawSegment(
        canvas: Canvas,
        paint: Paint,
        baseWidth: Float,
        baseAlpha: Int,
        intensity: Float,
    ) {
        paint.strokeWidth = baseWidth * widthScale
        paint.alpha = (baseAlpha * intensity).toInt().coerceIn(0, 255)
        canvas.drawLine(tail.x, tail.y, head.x, head.y, paint)
    }

    private fun trailLength(): Float {
        return hypot(head.x - tail.x, head.y - tail.y)
    }

    private fun approach(current: Float, target: Float, amount: Float): Float {
        return if (current < target) {
            minOf(target, current + amount)
        } else {
            maxOf(target, current - amount)
        }
    }

    private fun lerp(from: Float, to: Float, ratio: Float): Float {
        return from + (to - from) * ratio
    }

    companion object {
        private const val GLOW_WIDTH = 46f
        private const val RIBBON_WIDTH = 24f
        private const val CORE_WIDTH = 7f
        private const val TRAIL_APPEAR_SPEED = 12f
        private const val TRAIL_RETRACT_EASE = 9f
        private const val MIN_TRAIL_LENGTH = 8f
    }
}
