package com.example.klo_kursach

import android.app.Application
import com.example.klo_kursach.util.ThemeManager

/** Класс приложения: применяет сохранённую тему (светлая / тёмная / системная) до показа первого экрана. */
class KloApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applySaved(this)
    }
}
