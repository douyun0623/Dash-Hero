package com.example.dashhero

import com.example.dashhero.game.scene.MainScene
import com.example.dashhero.game.sound.SoundEffects
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainActivity : BaseGameActivity() {
    override val drawsDebugGrid = BuildConfig.DRAWS_DEBUG_GRID
    override val drawsDebugInfo = BuildConfig.DRAWS_DEBUG_INFO
    override val drawsFpsGraph = BuildConfig.DRAWS_FPS_GRAPH

    override fun createRootScene(gctx: GameContext): Scene {
        gctx.metrics.setSize(900f, 1600f)
        SoundEffects.init(this)
        return MainScene(gctx)
    }

    override fun onDestroy() {
        SoundEffects.release()
        super.onDestroy()
    }
}
