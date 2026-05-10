package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
    private val ribbonPath = Path()

    private val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 110, 70)
        style = Paint.Style.FILL
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

        drawFlagTrail(canvas)
    }

    private fun drawFlagTrail(canvas: Canvas) {
        val length = trailLength()
        val dx = (head.x - tail.x) / length
        val dy = (head.y - tail.y) / length
        val normalX = -dy
        val normalY = dx
        val tailHalfWidth = FLAG_HEAD_WIDTH * TAIL_WIDTH_RATIO * widthScale / 2f
        val headHalfWidth = FLAG_HEAD_WIDTH * widthScale / 2f

        ribbonPath.reset()
        ribbonPath.moveTo(tail.x + normalX * tailHalfWidth, tail.y + normalY * tailHalfWidth)
        ribbonPath.lineTo(head.x + normalX * headHalfWidth, head.y + normalY * headHalfWidth)
        ribbonPath.lineTo(head.x - normalX * headHalfWidth, head.y - normalY * headHalfWidth)
        ribbonPath.lineTo(tail.x - normalX * tailHalfWidth, tail.y - normalY * tailHalfWidth)
        ribbonPath.close()

        ribbonPaint.alpha = (190 * alphaScale).toInt().coerceIn(0, 255)
        canvas.drawPath(ribbonPath, ribbonPaint)
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
        private const val FLAG_HEAD_WIDTH = 55f
        private const val TAIL_WIDTH_RATIO = 0.1f
        private const val TRAIL_APPEAR_SPEED = 12f
        private const val TRAIL_RETRACT_EASE = 9f
        private const val MIN_TRAIL_LENGTH = 8f
    }
}
