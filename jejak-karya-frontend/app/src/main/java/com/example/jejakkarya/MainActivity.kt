package com.example.jejakkarya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.jejakkarya.navigation.AppNavigation
import com.example.jejakkarya.ui.theme.JejakKaryaTheme
import com.example.jejakkarya.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inisialisasi SettingsViewModel di tingkat aktivitas
        val settingsViewModel: SettingsViewModel by viewModels()

        setContent {
            // Amati state isDarkTheme
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            JejakKaryaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsViewModel)
                }
            }
        }
    }
}