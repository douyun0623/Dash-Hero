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

    fun getTopScores(): List<Int> {
        if (!::prefs.isInitialized) return listOf(0, 0, 0)
        // Backwards compatibility: load KEY_HIGH_SCORE if high_score_1 is not yet set
        val score1 = prefs.getInt("high_score_1", prefs.getInt(KEY_HIGH_SCORE, 0))
        val score2 = prefs.getInt("high_score_2", 0)
        val score3 = prefs.getInt("high_score_3", 0)
        return listOf(score1, score2, score3)
    }

    fun updateHighScore(score: Int): Boolean {
        if (!::prefs.isInitialized) return false
        val currentScores = getTopScores().toMutableList()
        
        currentScores.add(score)
        currentScores.sortDescending()
        val top3 = currentScores.take(3)
        
        val editor = prefs.edit()
        editor.putInt("high_score_1", top3[0])
        editor.putInt("high_score_2", top3[1])
        editor.putInt("high_score_3", top3[2])
        // Sync legacy key with 1st place score
        editor.putInt(KEY_HIGH_SCORE, top3[0])
        editor.apply()
        
        // Return true if it became the new 1st place high score
        return top3[0] == score
    }
}
