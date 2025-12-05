package uk.ac.tees.mad.locknote.utils

import android.content.Context

object ThemeUtils {
    private const val PREF_NAME = "user_prefs"
    private const val THEME_KEY = "theme_mode"

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(THEME_KEY, false)
    }

    fun toggleTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = prefs.getBoolean(THEME_KEY, false)
        prefs.edit().putBoolean(THEME_KEY, !current).apply()
    }
}
