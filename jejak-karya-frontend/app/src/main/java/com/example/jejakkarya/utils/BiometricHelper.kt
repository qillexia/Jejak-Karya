package com.example.jejakkarya.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object BiometricHelper {
    private const val PREFS_NAME = "biometric_prefs"
    private const val KEY_EMAIL = "saved_email"
    private const val KEY_PASSWORD = "saved_password"

    private fun getEncryptedSharedPreferences(context: Context): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun hasSavedCredentials(context: Context): Boolean {
        val prefs = getEncryptedSharedPreferences(context)
        return prefs.contains(KEY_EMAIL) && prefs.contains(KEY_PASSWORD)
    }

    fun saveCredentials(context: Context, email: String, pass: String) {
        val prefs = getEncryptedSharedPreferences(context)
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, pass)
            .apply()
    }
    
    fun clearCredentials(context: Context) {
        val prefs = getEncryptedSharedPreferences(context)
        prefs.edit().clear().apply()
    }

    fun getSavedCredentials(context: Context): Pair<String, String>? {
        val prefs = getEncryptedSharedPreferences(context)
        val email = prefs.getString(KEY_EMAIL, null)
        val pass = prefs.getString(KEY_PASSWORD, null)
        if (email != null && pass != null) return Pair(email, pass)
        return null
    }

    fun promptBiometric(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Autentikasi gagal. Silakan coba lagi.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login dengan Sidik Jari")
            .setSubtitle("Gunakan sidik jari Anda untuk masuk ke Jejak Karya")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
