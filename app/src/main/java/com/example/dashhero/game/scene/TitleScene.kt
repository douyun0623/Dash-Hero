package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.dashhero.R
import com.example.dashhero.game.objects.DashScrollBackground
import com.example.dashhero.game.util.HighScoreManager
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class TitleScene(gctx: GameContext) : Scene(gctx) {
    private val background = DashScrollBackground(gctx, R.drawable.bg_dash_city, -150f)

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 100f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(10f, 0f, 5f, Color.BLACK)
    }

    private val subTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 110, 70) // Orange color
        textAlign = Paint.Align.CENTER
        textSize = 42f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }

    private val boardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val boardBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(160, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val leaderboardHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 38f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }

    private val goldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 215, 0)
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }

    private val silverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(200, 200, 200)
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }

    private val bronzePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(205, 127, 50)
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }

    private val promptPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(210, 224, 255)
        textAlign = Paint.Align.CENTER
        textSize = 36f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private var promptBlinkTime = 0f

    override fun update(gctx: GameContext) {
        background.update(gctx)
        promptBlinkTime += gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        background.draw(canvas)

        val cx = gctx.metrics.width / 2f

        // Draw title
        canvas.drawText("DASH HERO", cx, 330f, titlePaint)
        canvas.drawText("Runner Action Game", cx, 400f, subTitlePaint)

        // Draw Leaderboard Card
        val cardLeft = cx - 260f
        val cardRight = cx + 260f
        val cardTop = 500f
        val cardBottom = 880f
        
        // Background card
        canvas.drawRoundRect(cardLeft, cardTop, cardRight, cardBottom, 24f, 24f, boardPaint)
        canvas.drawRoundRect(cardLeft, cardTop, cardRight, cardBottom, 24f, 24f, boardBorderPaint)
        
        // Header
        canvas.drawText("★ LEADERBOARD ★", cx, 565f, leaderboardHeaderPaint)
        
        // Top 3 Scores
        val scores = HighScoreManager.getTopScores()
        canvas.drawText("1st  ${scores.getOrElse(0) { 0 }}m", cx, 645f, goldPaint)
        canvas.drawText("2nd  ${scores.getOrElse(1) { 0 }}m", cx, 720f, silverPaint)
        canvas.drawText("3rd  ${scores.getOrElse(2) { 0 }}m", cx, 795f, bronzePaint)

        // Blinking tap to start prompt
        if ((promptBlinkTime * 2f).toInt() % 2 == 0) {
            canvas.drawText("Tap to Start", cx, 1050f, promptPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Transition to MainScene
            gctx.sceneStack.change(MainScene(gctx))
            return true
        }
        return false
    }
}
