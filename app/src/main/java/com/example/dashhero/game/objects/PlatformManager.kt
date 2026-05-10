package com.example.dashhero.game.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class PlatformManager(private val screenWidth: Float) : IGameObject {
    private val platforms = mutableListOf<GroundPlatform>()
    private val enemies = mutableListOf<Enemy>()
    private var lastX = 0f
    private val unitWidth = 200f
    private val platformHeight = 60f
    private val platformY = 1210f
    
    // 맵 데이터: X는 땅, -는 빈 공간
    private val mapData = "XXXXXXXXXX---XXXXX---XXXXXXXXXX---XXXX----XXXXXX"
    private var mapIndex = 0
    private var spawnCount = 0 // 생성된 총 발판/슬롯 수

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
            
            // 초기 5개 슬롯 이후부터 30% 확률로 적 스폰
            if (spawnCount > 5 && Math.random() < 0.3) {
                enemies.add(Enemy(centerX, platformY - 200f))
            }
        }
        
        lastX += unitWidth
        mapIndex = (mapIndex + 1) % mapData.length
        spawnCount++
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
        platforms.forEach { it.scrollBy(distance) }
        enemies.forEach { it.scrollBy(distance) }
        
        // 부족한 발판 보충
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
        
        // 화면 밖 발판 정리
        platforms.removeAll { it.isOffScreen() }
        enemies.removeAll { it.isOffScreen() }
    }

    fun updateEnemies(gctx: GameContext) {
        enemies.forEach { it.updateWithCollision(gctx, this) }
    }
    
    fun getEnemies(): List<Enemy> = enemies

    override fun update(gctx: GameContext) {
        // PlatformManager 자체의 update에서는 스크롤 처리를 MainScene에서 scrollBy로 호출하므로 
        // 개별 발판의 update(자체 이동 등)가 필요하다면 호출
        platforms.forEach { it.update(gctx) }
    }

    override fun draw(canvas: Canvas) {
        platforms.forEach { it.draw(canvas) }
        enemies.forEach { it.draw(canvas) }
    }
}
