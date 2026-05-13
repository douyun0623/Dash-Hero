package com.example.dashhero.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class ParticleSystem : IGameObject {
    class Particle(
        var x: Float,
        var y: Float,
        val vx: Float,
        val vy: Float,
        val color: Int,
        val radius: Float,
        val maxLife: Float
    ) {
        var life = maxLife
        val isDead: Boolean get() = life <= 0f

        fun update(dt: Float) {
            x += vx * dt
            y += vy * dt
            life -= dt
        }
    }

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun spawnExplosion(x: Float, y: Float, colors: IntArray, count: Int) {
        for (i in 0 until count) {
            val angle = (Math.random() * 2.0 * Math.PI).toFloat()
            val speed = 150f + Math.random().toFloat() * 700f
            val vx = Math.cos(angle.toDouble()).toFloat() * speed
            val vy = Math.sin(angle.toDouble()).toFloat() * speed
            val radius = 4f + Math.random().toFloat() * 10f
            val maxLife = 0.2f + Math.random().toFloat() * 0.25f
            val color = colors[i % colors.size]
            particles.add(Particle(x, y, vx, vy, color, radius, maxLife))
        }
    }

    fun scrollBy(distance: Float) {
        for (p in particles) {
            p.x -= distance
        }
    }

    override fun update(gctx: GameContext) {
        val dt = gctx.frameTime
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.update(dt)
            if (p.isDead) {
                iterator.remove()
            }
        }
    }

    override fun draw(canvas: Canvas) {
        for (p in particles) {
            paint.color = p.color
            paint.alpha = (255 * (p.life / p.maxLife).coerceIn(0f, 1f)).toInt()
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }
    }
}
