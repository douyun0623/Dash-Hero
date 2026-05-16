package com.example.dashhero.game.util

import android.content.Context
import android.content.SharedPreferences

object HighScoreManager {
    private const val PREFS_NAME = "dash_hero_prefs"
    private const val KEY_HIGH_SCORE = "high_score"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getHighScore(): Int {
        if (!::prefs.isInitialized) return 0
        return prefs.getInt(KEY_HIGH_SCORE, 0)
    }

    fun updateHighScore(score: Int): Boolean {
        if (!::prefs.isInitialized) return false
        val currentHighScore = getHighScore()
        if (score > currentHighScore) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply()
            return true
        }
        return false
    }
}
