package com.example.jejakkarya.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jejak_karya_settings", Context.MODE_PRIVATE)

    var isDarkTheme: Boolean
        get() = prefs.getBoolean("is_dark_theme", false) // Default light theme
        set(value) = prefs.edit().putBoolean("is_dark_theme", value).apply()

    var language: String
        get() = prefs.getString("app_language", "id") ?: "id" // "id" for Indonesia, "en" for English
        set(value) = prefs.edit().putString("app_language", value).apply()
}
