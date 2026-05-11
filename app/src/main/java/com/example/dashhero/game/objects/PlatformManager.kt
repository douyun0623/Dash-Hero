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
    
    // 다양한 발판 패턴 프리셋 정의
    private val patternPresets = listOf(
        "XXXXXXXXXX", // 평탄한 안전지대
        "XXXX---XXX", // 기본 낙사 구간
        "XX-X-X-XX",  // 징검다리 구간
        "XXXHHHXXX",  // 고지대 언덕 구간
        "XXLL-HHXX",  // 큰 고저차 및 낙사 조합
        "X-H-L-H-X",  // 엇박자 공중 발판 구간
        "XX-HH-L-XX"  // 복합 장애물 구간
    )
    private var currentPattern = "XXXXXXXXXX" // 첫 패턴은 안전지대로 시작
    private var patternIndex = 0
    private var spawnCount = 0 // 생성된 총 발판/슬롯 수

    init {
        // 초기 발판 생성 (화면을 채울 정도)
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
    }

    private fun spawnNext() {
        if (patternIndex >= currentPattern.length) {
            // 새 패턴 조각 무작위 선택
            currentPattern = patternPresets.random()
            patternIndex = 0
        }

        val char = currentPattern[patternIndex]
        if (char != '-') {
            // 기호에 따라 발판 높낮이 계산
            val heightOffset = when (char) {
                'H' -> -150f // 고지대
                'L' -> 120f  // 저지대
                else -> 0f   // 평지 ('X')
            }
            val targetY = platformY + heightOffset
            val centerX = lastX + unitWidth / 2f
            platforms.add(GroundPlatform(centerX, targetY, unitWidth + 2f, platformHeight)) // 이음새 방지를 위해 +2f
            
            // 초기 5개 슬롯 이후부터 적 스폰
            // 고지대('H')에서는 조금 더 높은 확률(40%)로 적 배치
            val spawnChance = if (char == 'H') 0.4 else 0.3
            if (spawnCount > 5 && Math.random() < spawnChance) {
                enemies.add(Enemy(centerX, targetY - 200f))
            }
        }
        
        lastX += unitWidth
        patternIndex++
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
