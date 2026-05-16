package com.example.dashhero.game.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class PauseScene(gctx: GameContext) : Scene(gctx) {
    private val overlayPaint = Paint().apply {
        color = Color.argb(160, 0, 0, 0)
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 90f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(10f, 0f, 5f, Color.BLACK)
    }

    private val buttonBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 50, 70, 100)
        style = Paint.Style.FILL
    }

    private val buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 40f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val resumeRect = RectF(250f, 700f, 650f, 800f)
    private val restartRect = RectF(250f, 850f, 650f, 950f)
    private val exitRect = RectF(200f, 1000f, 700f, 1100f)

    override fun update(gctx: GameContext) {
        // 일시정지 중이므로 내부 상태 업데이트 없음
    }

    override fun draw(canvas: Canvas) {
        // 1. 이전 씬(MainScene) 먼저 그리기
        gctx.sceneStack.getPreviousScene()?.draw(canvas)

        // 2. 반투명 어두운 오버레이 깔기
        canvas.drawRect(gctx.metrics.borderRect, overlayPaint)

        val cx = gctx.metrics.width / 2f

        // 3. 타이틀 출력
        canvas.drawText("PAUSED", cx, 500f, titlePaint)

        // 4. 버튼 그리기 (둥근 사각형 + 텍스트)
        canvas.drawRoundRect(resumeRect, 20f, 20f, buttonBgPaint)
        canvas.drawText("Resume", cx, 765f, buttonTextPaint)

        canvas.drawRoundRect(restartRect, 20f, 20f, buttonBgPaint)
        canvas.drawText("Restart", cx, 915f, buttonTextPaint)

        canvas.drawRoundRect(exitRect, 20f, 20f, buttonBgPaint)
        canvas.drawText("Exit to Title", cx, 1065f, buttonTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val tx = event.x
            val ty = event.y

            // GameMetrics를 통한 가상 좌표 변환 적용
            val gameCoord = gctx.metrics.fromScreen(tx, ty)
            val gx = gameCoord.x
            val gy = gameCoord.y

            if (resumeRect.contains(gx, gy)) {
                pop() // 일시정지 해제
                return true
            } else if (restartRect.contains(gx, gy)) {
                gctx.sceneStack.change(MainScene(gctx)) // 게임 재시작
                return true
            } else if (exitRect.contains(gx, gy)) {
                gctx.sceneStack.change(TitleScene(gctx)) // 타이틀로 이동
                return true
            }
        }
        return false
    }
}
