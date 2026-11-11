package com.example.klo_kursach.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * Управление светлой / тёмной темой.
 * Значение хранится в настройках по ключу [KEY] (тот же ключ использует экран настроек).
 */
object ThemeManager {
    const val KEY = "theme"
    const val SYSTEM = "system"
    const val LIGHT = "light"
    const val DARK = "dark"

    fun apply(mode: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun applySaved(context: Context) {
        apply(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, SYSTEM) ?: SYSTEM)
    }

    /** Быстрое переключение из меню: тёмная ⇄ светлая. */
    fun toggle(context: Context) {
        val isNight = (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val newMode = if (isNight) LIGHT else DARK
        PreferenceManager.getDefaultSharedPreferences(context).edit { putString(KEY, newMode) }
        apply(newMode)
    }
}
