package com.example.jejakkarya.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jejakkarya.R
import com.example.jejakkarya.ui.viewmodel.AuthState
import com.example.jejakkarya.ui.viewmodel.AuthViewModel
import androidx.fragment.app.FragmentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.jejakkarya.utils.BiometricHelper
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showBiometricOptIn by remember { mutableStateOf(false) }
    var isLoginBiometric by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
                
                val activity = context as? FragmentActivity
                if (activity != null && !isLoginBiometric && BiometricHelper.isBiometricAvailable(activity) && !BiometricHelper.hasSavedCredentials(activity)) {
                    showBiometricOptIn = true
                } else {
                    onNavigateToHome()
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
                isLoginBiometric = false
            }
            else -> {}
        }
    }
    
    if (showBiometricOptIn) {
        AlertDialog(
            onDismissRequest = { 
                showBiometricOptIn = false
                onNavigateToHome() 
            },
            title = { Text("Aktifkan Login Sidik Jari?") },
            text = { Text("Anda dapat menggunakan sidik jari untuk login instan di masa mendatang.") },
            confirmButton = {
                TextButton(onClick = {
                    val activity = context as? FragmentActivity
                    if (activity != null) {
                        BiometricHelper.promptBiometric(activity, onSuccess = {
                            BiometricHelper.saveCredentials(activity, email, password)
                            Toast.makeText(context, "Sidik Jari diaktifkan", Toast.LENGTH_SHORT).show()
                            showBiometricOptIn = false
                            onNavigateToHome()
                        }, onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        })
                    }
                }) { Text("Aktifkan") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showBiometricOptIn = false
                    onNavigateToHome()
                }) { Text("Lain Kali") }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image 
        Image(
            painter = painterResource(id = R.drawable.background3),
            contentDescription = "Background Login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Gradient Overlay untuk kontras teks & tombol
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.95f)
                        ),
                        startY = 0f
                    )
                )
        )

        // Konten Tengah dan Bawah
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bagian Atas: Logo & Nama Aplikasi
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 90.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.jejak_karuhun),
                    contentDescription = "Logo Jejak Karya",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Jejak Karya",
                    color = Color.White,
                    fontSize = 43.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gerbang Pintar Menjelajahi Sejarah Seni",
                    color = Color(0xFFD4C5B9),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(150.dp))

            // Bagian Bawah: Form Login
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                androidx.compose.material3.OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color(0xFFD4C5B9)) },
                    singleLine = true,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFC75A3A),
                        unfocusedBorderColor = Color(0xFF7A6B62),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFC75A3A),
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color(0xFFD4C5B9)) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            androidx.compose.material3.Icon(
                                imageVector = if (isPasswordVisible) androidx.compose.material.icons.Icons.Filled.VisibilityOff else androidx.compose.material.icons.Icons.Filled.Visibility,
                                contentDescription = "Toggle Password Visibility",
                                tint = Color(0xFFD4C5B9)
                            )
                        }
                    },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFC75A3A),
                        unfocusedBorderColor = Color(0xFF7A6B62),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFC75A3A),
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Login Button
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { 
                            if (email.isNotBlank() && password.isNotBlank()) {
                                if (!email.endsWith("@gmail.com")) {
                                    Toast.makeText(context, "Gagal: Email harus berakhiran @gmail.com", Toast.LENGTH_SHORT).show()
                                } else {
                                    isLoginBiometric = false
                                    authViewModel.login(email, password) 
                                }
                            } else {
                                Toast.makeText(context, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC75A3A),
                            contentColor = Color.White
                        ),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Masuk",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    val activity = context as? FragmentActivity
                    if (activity != null && BiometricHelper.isBiometricAvailable(activity) && BiometricHelper.hasSavedCredentials(activity)) {
                        IconButton(
                            onClick = {
                                BiometricHelper.promptBiometric(activity, onSuccess = {
                                    val creds = BiometricHelper.getSavedCredentials(activity)
                                    if (creds != null) {
                                        email = creds.first
                                        password = creds.second
                                        isLoginBiometric = true
                                        authViewModel.login(email, password)
                                    }
                                }, onError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                })
                            },
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFC75A3A).copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Filled.Fingerprint, contentDescription = "Biometric Login", tint = Color(0xFFC75A3A), modifier = Modifier.size(32.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Belum punya akun? ",
                        color = Color(0xFF7A6B62),
                        fontSize = 14.sp
                    )
                    androidx.compose.foundation.text.ClickableText(
                        text = androidx.compose.ui.text.AnnotatedString("Daftar di sini"),
                        onClick = { onNavigateToRegister() },
                        style = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFFC75A3A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
