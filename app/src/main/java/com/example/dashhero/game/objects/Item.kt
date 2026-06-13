package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

enum class ItemType {
    BATTERY,
    MAGNET,
    STAR
}

class Item(
    var x: Float,
    var y: Float,
    val type: ItemType,
    var parentPlatform: GroundPlatform? = null
) : IGameObject, IBoxCollidable {
    private val width = 50f
    private val height = 75f
    private val bounds = RectF()
    var isAlive = true
        private set

    private val relativeX = if (parentPlatform != null) x - parentPlatform!!.x else 0f
    private val relativeY = if (parentPlatform != null) y - parentPlatform!!.y else 0f

    // --- Paint Styles ---
    private val batteryBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(0, 220, 255)
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.rgb(0, 220, 255))
    }
    private val batteryCapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(220, 220, 220)
        style = Paint.Style.FILL
    }
    private val batteryBoltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private val magnetRedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 60, 60) // Red magnet body
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.rgb(255, 60, 60))
    }
    private val magnetBluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(60, 100, 255) // Blue polar tip (S pole)
        style = Paint.Style.FILL
    }
    private val magnetSilverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(220, 220, 220) // Silver polar tip (N pole)
        style = Paint.Style.FILL
    }

    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 225, 40) // Yellow star
        style = Paint.Style.FILL
        setShadowLayer(12f, 0f, 0f, Color.rgb(255, 225, 40))
    }

    fun collect() {
        isAlive = false
    }

    fun scrollBy(distance: Float) {
        if (parentPlatform == null) {
            x -= distance
        }
    }

    fun isOffScreen(): Boolean {
        return x < -200f
    }

    fun updateWithMagnet(playerX: Float, playerY: Float, isMagnetActive: Boolean, dt: Float) {
        val bobOffset = Math.sin(System.currentTimeMillis() * 0.007).toFloat() * 15f
        if (parentPlatform != null) {
            if (parentPlatform!!.isFell) {
                parentPlatform = null
            } else {
                x = parentPlatform!!.x + relativeX
                y = parentPlatform!!.y + relativeY + bobOffset
            }
        } else {
            y += Math.sin(System.currentTimeMillis() * 0.007).toFloat() * 0.4f
        }

        // 자석이 켜져 있으면 플레이어를 향해 빨려 들어감
        if (isMagnetActive && isAlive) {
            val dx = playerX - x
            val dy = playerY - y
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            if (dist < 750f && dist > 10f) {
                parentPlatform = null
                val pullSpeed = 1600f
                x += (dx / dist) * pullSpeed * dt
                y += (dy / dist) * pullSpeed * dt
            }
        }
    }

    override fun update(gctx: GameContext) {
        val bobOffset = Math.sin(System.currentTimeMillis() * 0.007).toFloat() * 15f
        if (parentPlatform != null) {
            if (parentPlatform!!.isFell) {
                parentPlatform = null
            } else {
                x = parentPlatform!!.x + relativeX
                y = parentPlatform!!.y + relativeY + bobOffset
            }
        } else {
            y += Math.sin(System.currentTimeMillis() * 0.007).toFloat() * 0.4f
        }
    }

    override fun draw(canvas: Canvas) {
        if (!isAlive) return

        when (type) {
            ItemType.BATTERY -> {
                // 1. 배터리 본체
                bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
                canvas.drawRoundRect(bounds, 12f, 12f, batteryBodyPaint)

                // 2. 단자
                canvas.drawRect(x - 10f, y - height / 2f - 8f, x + 10f, y - height / 2f, batteryCapPaint)

                // 3. 번개 데코
                val boltPath = Path().apply {
                    moveTo(x - 5f, y - 20f)
                    lineTo(x + 12f, y - 5f)
                    lineTo(x + 3f, y)
                    lineTo(x + 10f, y + 20f)
                    lineTo(x - 12f, y + 5f)
                    lineTo(x - 3f, y)
                    close()
                }
                canvas.drawPath(boltPath, batteryBoltPaint)
            }
            ItemType.MAGNET -> {
                // 1. 말굽 모양 U자 Path 그리기
                val uPath = Path().apply {
                    // 왼쪽 끝
                    moveTo(x - 22f, y - 20f)
                    lineTo(x - 22f, y + 10f)
                    quadTo(x - 22f, y + 24f, x, y + 24f)
                    quadTo(x + 22f, y + 24f, x + 22f, y + 10f)
                    lineTo(x + 22f, y - 20f)
                    // 우측 두께 및 내부 안쪽 파내기
                    lineTo(x + 10f, y - 20f)
                    lineTo(x + 10f, y + 8f)
                    quadTo(x + 10f, y + 8f + 5f, x, y + 8f + 5f)
                    quadTo(x - 10f, y + 8f + 5f, x - 10f, y + 8f)
                    lineTo(x - 10f, y - 20f)
                    close()
                }
                canvas.drawPath(uPath, magnetRedPaint)

                // 2. N극/S극 단자 팁 (자석의 윗부분 좌우 끝단)
                // 왼쪽 단자 (N극 - 은색)
                canvas.drawRect(x - 22f, y - 25f, x - 10f, y - 18f, magnetSilverPaint)
                // 오른쪽 단자 (S극 - 파란색)
                canvas.drawRect(x + 10f, y - 25f, x + 22f, y - 18f, magnetBluePaint)
            }
            ItemType.STAR -> {
                // 1. 오각별 그리기 (외경 28px, 내경 12px)
                val starPath = Path()
                val r = 28f
                val innerR = 12f
                for (step in 0 until 10) {
                    val angle = Math.PI * step / 5.0 - Math.PI / 2.0
                    val currR = if (step % 2 == 0) r else innerR
                    val px = (x + Math.cos(angle) * currR).toFloat()
                    val py = (y + Math.sin(angle) * currR).toFloat()
                    if (step == 0) {
                        starPath.moveTo(px, py)
                    } else {
                        starPath.lineTo(px, py)
                    }
                }
                starPath.close()
                canvas.drawPath(starPath, starPaint)
            }
        }
    }

    override val collisionRect: RectF
        get() {
            bounds.set(x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f)
            return bounds
        }
}
