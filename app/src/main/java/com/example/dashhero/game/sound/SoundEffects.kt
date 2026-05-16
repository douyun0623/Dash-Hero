package com.example.dashhero.game.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.dashhero.R

object SoundEffects {
    private var soundPool: SoundPool? = null
    private var sndJumpId = 0
    private var sndDashId = 0
    private var sndStompId = 0
    private var sndGameOverId = 0
    private var sndCollectId = 0

    fun init(context: Context) {
        if (soundPool != null) return

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()

        soundPool?.let { pool ->
            sndJumpId = pool.load(context, R.raw.snd_jump, 1)
            sndDashId = pool.load(context, R.raw.snd_dash, 1)
            sndStompId = pool.load(context, R.raw.snd_stomp, 1)
            sndGameOverId = pool.load(context, R.raw.snd_gameover, 1)
            sndCollectId = pool.load(context, R.raw.snd_collect, 1)
        }
    }

    fun playJump() {
        soundPool?.play(sndJumpId, 1f, 1f, 1, 0, 1f)
    }

    fun playDash() {
        soundPool?.play(sndDashId, 1f, 1f, 1, 0, 1f)
    }

    fun playStomp() {
        soundPool?.play(sndStompId, 1f, 1f, 1, 0, 1f)
    }

    fun playGameOver() {
        soundPool?.play(sndGameOverId, 1f, 1f, 2, 0, 1f)
    }

    fun playCollect() {
        soundPool?.play(sndCollectId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
