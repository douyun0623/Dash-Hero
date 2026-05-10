package com.example.dashhero.game.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class PlatformManager(private val screenWidth: Float) : IGameObject {
    private val platforms = mutableListOf<GroundPlatform>()
    private var lastX = 0f
    private val unitWidth = 200f
    private val platformHeight = 60f
    private val platformY = 1210f
    
    // 맵 데이터: X는 땅, -는 빈 공간
    private val mapData = "XXXXXXXXXX---XXXXX---XXXXXXXXXX---XXXX----XXXXXX"
    private var mapIndex = 0

    init {
        // 초기 발판 생성 (화면을 채울 정도)
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
    }

    private fun spawnNext() {
        val char = mapData[mapIndex]
        if (char == 'X') {
            // 발판 생성. GroundPlatform의 중심 좌표 x를 계산
            val centerX = lastX + unitWidth / 2f
            platforms.add(GroundPlatform(centerX, platformY, unitWidth + 2f, platformHeight)) // 이음새 방지를 위해 +2f
        }
        
        lastX += unitWidth
        mapIndex = (mapIndex + 1) % mapData.length
    }

    fun getPlatformAt(x: Float): GroundPlatform? {
        return platforms.find { p ->
            val left = p.screenX - p.width / 2f
            val right = p.screenX + p.width / 2f
            x in left..right
        }
    }

    fun scrollBy(distance: Float) {
        lastX -= distance
        val iterator = platforms.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.scrollBy(distance)
            
            // 화면 왼쪽으로 완전히 나간 발판 제거
            // GroundPlatform 내부 x는 중심점이므로 x + width/2가 0보다 작으면 제거
            // 하지만 GroundPlatform 내부의 width에 접근하기 어려우므로 대략적인 값 사용 
            // 또는 GroundPlatform을 수정하여 체크
        }
        
        // 부족한 발판 보충
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
        
        // 화면 밖 발판 정리
        platforms.removeAll { it.isOffScreen() }
    }

    override fun update(gctx: GameContext) {
        // PlatformManager 자체의 update에서는 스크롤 처리를 MainScene에서 scrollBy로 호출하므로 
        // 개별 발판의 update(자체 이동 등)가 필요하다면 호출
        platforms.forEach { it.update(gctx) }
    }

    override fun draw(canvas: Canvas) {
        platforms.forEach { it.draw(canvas) }
    }
}
