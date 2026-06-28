package com.example.jejakkarya.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        Spacer(modifier = Modifier.height(32.dp))
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.jejakkarya.R.drawable.jejak_karuhun),
                contentDescription = "Logo Jejak Karya",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Jejak Karya",
                color = Color(0xFF4A3B32),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive
            )
        }
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
