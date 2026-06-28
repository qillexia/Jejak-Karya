package com.example.jejakkarya.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jejakkarya.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import com.example.jejakkarya.utils.BiometricHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val username: String,
    val email: String
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val supabase = SupabaseClient.client
    private val sharedPref: SharedPreferences = application.getSharedPreferences("JejakKaryaPrefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // Reset state back to idle
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                val uid = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val profile = supabase.postgrest["users_profile"]
                    .select { filter { eq("id", uid) } }
                    .decodeSingleOrNull<UserProfile>()
                _userProfile.value = profile
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }

    fun updateProfile(name: String, username: String, password: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val uid = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Sesi tidak valid")
                supabase.postgrest["users_profile"].update(
                    {
                        set("name", name)
                        set("username", username)
                    }
                ) {
                    filter { eq("id", uid) }
                }
                
                if (!password.isNullOrBlank()) {
                    supabase.auth.updateUser {
                        this.password = password
                    }
                    // Update biometric credentials jika aktif
                    val context = getApplication<Application>()
                    if (BiometricHelper.hasSavedCredentials(context)) {
                        val currentEmail = _userProfile.value?.email
                        if (currentEmail != null) {
                            BiometricHelper.saveCredentials(context, currentEmail, password)
                        }
                    }
                }
                
                fetchProfile() // Update state lokal
                _authState.value = AuthState.Success("Profil berhasil diperbarui!")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Gagal memperbarui profil")
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    password = pass
                }
                sharedPref.edit().putBoolean("is_logged_in", true).apply()
                _authState.value = AuthState.Success("Login berhasil!")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun register(name: String, username: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 1. Register auth user
                val authUser = supabase.auth.signUpWith(Email) {
                    this.email = email
                    password = pass
                }
                
                // 2. Insert into users_profile
                // Jika "Confirm Email" aktif di Supabase, signUpWith mengembalikan UserInfo (ada id).
                // Jika "Confirm Email" mati, signUpWith mengembalikan null, tapi currentUserOrNull() akan berisi session.
                val uid = authUser?.id ?: supabase.auth.currentUserOrNull()?.id 
                    ?: throw Exception("Gagal mendapatkan UID (Cek pengaturan Email Confirm di Supabase)")
                
                val profile = UserProfile(
                    id = uid,
                    name = name,
                    username = username,
                    email = email
                )
                
                supabase.postgrest["users_profile"].insert(profile)
                
                sharedPref.edit().putBoolean("is_logged_in", true).apply()
                _authState.value = AuthState.Success("Pendaftaran berhasil! Silakan login.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Pendaftaran gagal")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                sharedPref.edit().putBoolean("is_logged_in", false).apply()
                _userProfile.value = null
            } catch (e: Exception) {
                // Ignore errors on logout
            }
        }
    }
}
