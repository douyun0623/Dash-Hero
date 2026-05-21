package com.example.dashhero

import android.media.MediaPlayer
import com.example.dashhero.game.scene.MainScene
import com.example.dashhero.game.scene.TitleScene
import com.example.dashhero.game.sound.SoundEffects
import com.example.dashhero.game.util.HighScoreManager
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainActivity : BaseGameActivity() {
    override val drawsDebugGrid = BuildConfig.DRAWS_DEBUG_GRID
    override val drawsDebugInfo = BuildConfig.DRAWS_DEBUG_INFO
    override val drawsFpsGraph = BuildConfig.DRAWS_FPS_GRAPH

    private var bgmPlayer: MediaPlayer? = null

    override fun createRootScene(gctx: GameContext): Scene {
        gctx.metrics.setSize(900f, 1600f)
        SoundEffects.init(this)
        HighScoreManager.init(this)
        return TitleScene(gctx)
    }

    private fun initBgm() {
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(this, R.raw.bgm_main).apply {
                isLooping = true
                setVolume(0.5f, 0.5f) // Set comfortable background volume
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initBgm()
        bgmPlayer?.start()
    }

    override fun onPause() {
        bgmPlayer?.pause()
        super.onPause()
    }

    override fun onDestroy() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
        SoundEffects.release()
        super.onDestroy()
    }
}
