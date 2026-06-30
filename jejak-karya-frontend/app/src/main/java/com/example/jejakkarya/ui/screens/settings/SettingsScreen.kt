package com.example.jejakkarya.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jejakkarya.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val language by viewModel.language.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // Clear Cache Setting
            SettingItem(
                icon = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = Color(0xFF994121)) },
                title = "Hapus Cache",
                subtitle = "Kosongkan memori sementara aplikasi",
                action = {
                    Button(
                        onClick = { showClearCacheDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF994121), contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.Bold)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Setting
            SettingItem(
                icon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF994121)) },
                title = "Ubah Data Akun",
                subtitle = "Perbarui profil dan kata sandi Anda",
                action = {
                    Button(
                        onClick = onEditProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF994121), contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ubah", fontWeight = FontWeight.Bold)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Setting
            SettingItem(
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                title = "Keluar Akun",
                subtitle = "Akhiri sesi dan kembali ke login",
                action = {
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Keluar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Dialog Konfirmasi Hapus Cache
        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                title = { Text(text = "Hapus Cache") },
                text = { Text(text = "Apakah Anda yakin ingin menghapus cache memori? Semua data karya seni yang tersimpan offline akan diunduh ulang saat Anda online.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearCache()
                            showClearCacheDialog = false
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCacheDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Dialog Konfirmasi Keluar Akun
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Keluar Akun") },
                text = { Text(text = "Apakah Anda yakin ingin keluar dari Jejak Karya? Anda perlu login kembali untuk mengakses data Anda.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }
                    ) {
                        Text("Keluar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), lineHeight = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            action()
        }
    }
}
