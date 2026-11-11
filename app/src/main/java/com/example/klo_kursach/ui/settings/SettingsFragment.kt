package com.example.klo_kursach.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.klo_kursach.R
import com.example.klo_kursach.util.ThemeManager
import com.example.klo_kursach.util.appVersion

/** Настройки приложения: тема, шрифт, офлайн-режим, экспорт, OCR и ИИ. */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(ThemeManager.KEY)?.setOnPreferenceChangeListener { _, newValue ->
            ThemeManager.apply(newValue as String)
            true
        }
        findPreference<Preference>("version")?.summary = requireContext().appVersion()
    }
}
