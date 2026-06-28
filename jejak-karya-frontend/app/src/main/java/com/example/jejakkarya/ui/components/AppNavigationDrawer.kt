package com.example.jejakkarya.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppNavigationDrawerContent(
    onNavigate: (String) -> Unit = {}
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFFFFFFF)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Jejak Karya",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            color = Color(0xFF4A3B32),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp), color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        NavigationDrawerItem(
            label = { Text("Sinkronisasi Data", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("refresh") },
            icon = { Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Pengaturan", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("settings") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Notifikasi", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("notifications") },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Panduan Bantuan", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("help") },
            icon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Syarat & Ketentuan", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("terms") },
            icon = { Icon(Icons.Outlined.Policy, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Tentang Aplikasi", color = Color(0xFF4A3B32)) },
            selected = false,
            onClick = { onNavigate("about") },
            icon = { Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFF4A3B32)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp), color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        NavigationDrawerItem(
            label = { Text("Keluar", color = Color.Red) },
            selected = false,
            onClick = { onNavigate("logout") },
            icon = { Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding).padding(bottom = 16.dp)
        )
    }
}
