package com.example.jejakkarya.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jejakkarya.data.local.PreferencesHelper
import com.example.jejakkarya.data.local.StorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesHelper = PreferencesHelper(application)
    private val storageHelper = StorageHelper(application)

    private val _isDarkTheme = MutableStateFlow(preferencesHelper.isDarkTheme)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _language = MutableStateFlow(preferencesHelper.language)
    val language: StateFlow<String> = _language

    private val _toastMessage = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val toastMessage: kotlinx.coroutines.flow.SharedFlow<String> = _toastMessage

    fun toggleTheme(isDark: Boolean) {
        preferencesHelper.isDarkTheme = isDark
        _isDarkTheme.value = isDark
    }

    fun setLanguage(lang: String) {
        preferencesHelper.language = lang
        _language.value = lang
    }

    fun clearCache() {
        viewModelScope.launch {
            storageHelper.clearAllCache()
            _toastMessage.emit("Cache berhasil dibersihkan!")
        }
    }
}
