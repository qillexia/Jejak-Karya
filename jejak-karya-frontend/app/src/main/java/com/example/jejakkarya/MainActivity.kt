package com.example.jejakkarya

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.example.jejakkarya.navigation.AppNavigation
import com.example.jejakkarya.ui.theme.JejakKaryaTheme
import com.example.jejakkarya.ui.viewmodel.SettingsViewModel
import com.example.jejakkarya.ui.viewmodel.AuthViewModel
import com.example.jejakkarya.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inisialisasi SettingsViewModel di tingkat aktivitas
        val settingsViewModel: SettingsViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()
        
        val sharedPref = getSharedPreferences("JejakKaryaPrefs", android.content.Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        val startDestination = if (isLoggedIn) "home" else "splash"

        setContent {
            // Amati state isDarkTheme
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            JejakKaryaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsViewModel, authViewModel, startDestination)
                }
            }
        }
    }
}