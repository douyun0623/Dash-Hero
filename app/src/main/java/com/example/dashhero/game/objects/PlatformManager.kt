package com.example.dashhero.game.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class PlatformManager(private val screenWidth: Float) : IGameObject {
    private val platforms = mutableListOf<GroundPlatform>()
    private val enemies = mutableListOf<Enemy>()
    private val items = mutableListOf<Item>()
    private val flyingEnemies = mutableListOf<DroneEnemy>()
    private val spikes = mutableListOf<Spike>()
    private val spikyEnemies = mutableListOf<SpikyEnemy>()
    private var lastX = 0f
    private val unitWidth = 280f
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
    var playerScreenX: Float = 180f
    var isFeverMode: Boolean = false
    var difficultyLevel: Int = 0
    private var safeScrollCooldown: Float = 0f

    init {
        // 초기 발판 생성 (화면을 채울 정도)
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
    }

    fun updateSafeCooldown(dt: Float) {
        if (safeScrollCooldown > 0f) {
            safeScrollCooldown -= dt
            if (safeScrollCooldown < 0f) safeScrollCooldown = 0f
        }
    }

    fun startSafeCooldown(duration: Float) {
        safeScrollCooldown = duration
    }

    private val safePatterns = listOf(
        "XXXXXXXXXX",
        "XXXHHHXXX"
    )

    private fun spawnNext() {
        if (patternIndex >= currentPattern.length) {
            // 피버 중이거나 안전 쿨다운 중이면 gap 없는 안전 패턴만 사용
            currentPattern = if (isFeverMode || safeScrollCooldown > 0f) {
                safePatterns.random()
            } else {
                patternPresets.random()
            }
            patternIndex = 0
        }

        val char = currentPattern[patternIndex]
        if (char != '-') {
            // 기호에 따라 발판 높낮이 계산
            val heightOffset = when (char) {
                'H' -> -120f // 고지대
                'L' -> 100f  // 저지대
                else -> 0f   // 평지 ('X')
            }
            val targetY = platformY + heightOffset
            val centerX = lastX + unitWidth / 2f
            val randType = Math.random()
            val platformType = when {
                spawnCount <= 5 -> PlatformType.NORMAL
                isFeverMode -> PlatformType.NORMAL
                randType < 0.80 -> PlatformType.NORMAL
                randType < 0.90 -> PlatformType.MOVING_X
                else -> PlatformType.MOVING_Y
            }
            platforms.add(GroundPlatform(centerX, targetY, unitWidth + 2f, platformHeight, platformType)) // 이음새 방지를 위해 +2f
            
            // 플레이어와의 거리가 650px 이하인 너무 가까운 경우 적 스폰을 제한하여 복귀 시 억까 방지
            val distanceToPlayer = centerX - playerScreenX
            val isTooClose = distanceToPlayer < 650f
            
            // 초기 5개 슬롯 이후부터 적 스폰
            // 고지대('H')에서는 조금 더 높은 확률(40%)로 적 배치
            val adjustedEnemySpawnChance = (if (char == 'H') 0.4 else 0.3) + difficultyLevel * 0.05
            val enemySpawned = !isTooClose && spawnCount > 5 && Math.random() < adjustedEnemySpawnChance
            if (enemySpawned) {
                if (Math.random() < 0.35) {
                    spikyEnemies.add(SpikyEnemy(centerX, targetY - 200f))
                } else {
                    enemies.add(Enemy(centerX, targetY - 200f))
                }
            } else {
                val rand = Math.random()
                val droneChance = 0.20 + difficultyLevel * 0.04
                val spikeChance = 0.20 + difficultyLevel * 0.04
                if (spawnCount > 2 && rand < 0.35) {
                    val randType = Math.random()
                    val itemType = when {
                        randType < 0.70 -> ItemType.BATTERY
                        randType < 0.85 -> ItemType.MAGNET
                        else -> ItemType.STAR
                    }
                    items.add(Item(centerX, targetY - 140f, itemType))
                } else if (!isTooClose && spawnCount > 5 && rand < 0.35 + droneChance) { // 너무 가깝지 않을 때만 공중 적 배치
                    flyingEnemies.add(DroneEnemy(centerX, targetY - 260f))
                } else if (!isTooClose && spawnCount > 5 && rand < 0.35 + droneChance + spikeChance) { // 너무 가깝지 않을 때만 가시 배치
                    spikes.add(Spike(centerX, targetY - 60f))
                }
            }
        } else {
            // 낙사 구간('-') 상공에 40% 확률로 드론 스폰 (플레이어와 너무 가까우면 스폰 방지)
            val centerX = lastX + unitWidth / 2f
            val distanceToPlayer = centerX - playerScreenX
            val isTooClose = distanceToPlayer < 650f
            val adjustedGapDroneChance = 0.4 + difficultyLevel * 0.04
            if (!isTooClose && spawnCount > 5 && Math.random() < adjustedGapDroneChance) {
                flyingEnemies.add(DroneEnemy(centerX, platformY - 260f))
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
        items.forEach { it.scrollBy(distance) }
        flyingEnemies.forEach { it.scrollBy(distance) }
        spikes.forEach { it.scrollBy(distance) }
        spikyEnemies.forEach { it.scrollBy(distance) }
        
        // 부족한 발판 보충
        while (lastX < screenWidth * 1.5f) {
            spawnNext()
        }
        
        // 화면 밖 발판 정리
        platforms.removeAll { it.isOffScreen() }
        enemies.removeAll { it.isOffScreen() }
        items.removeAll { it.isOffScreen() || !it.isAlive }
        flyingEnemies.removeAll { it.isOffScreen() }
        spikes.removeAll { it.isOffScreen() }
        spikyEnemies.removeAll { it.isOffScreen() }
    }

    fun updateEnemies(gctx: GameContext) {
        enemies.forEach { it.updateWithCollision(gctx, this) }
    }

    fun updateSpikyEnemies(gctx: GameContext) {
        spikyEnemies.forEach { it.updateWithCollision(gctx, this) }
    }
    
    fun getEnemies(): List<Enemy> = enemies
    fun getSpikyEnemies(): List<SpikyEnemy> = spikyEnemies
    fun getItems(): List<Item> = items
    fun updateItems(playerX: Float, playerY: Float, isMagnetActive: Boolean, dt: Float) {
        items.forEach { it.updateWithMagnet(playerX, playerY, isMagnetActive, dt) }
    }
    fun getFlyingEnemies(): List<DroneEnemy> = flyingEnemies
    fun getSpikes(): List<Spike> = spikes

    override fun update(gctx: GameContext) {
        // PlatformManager 자체의 update에서는 스크롤 처리를 MainScene에서 scrollBy로 호출하므로 
        // 개별 발판의 update(자체 이동 등)가 필요하다면 호출
        platforms.forEach { it.update(gctx) }
        items.forEach { it.update(gctx) }
        flyingEnemies.forEach { it.update(gctx) }
        spikes.forEach { it.update(gctx) }
        spikyEnemies.forEach { it.update(gctx) }
    }

    override fun draw(canvas: Canvas) {
        platforms.forEach { it.draw(canvas) }
        items.forEach { it.draw(canvas) }
        flyingEnemies.forEach { it.draw(canvas) }
        enemies.forEach { it.draw(canvas) }
        spikes.forEach { it.draw(canvas) }
        spikyEnemies.forEach { it.draw(canvas) }
    }
}
