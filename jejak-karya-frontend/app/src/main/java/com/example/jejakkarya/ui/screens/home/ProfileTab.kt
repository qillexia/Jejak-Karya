package com.example.jejakkarya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    modifier: Modifier = Modifier,
    onNavigateToAbout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Foto Profil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5D5C5))
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "G",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF994121)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nama
        Text(
            text = "Guest User",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A3B32)
        )
        
        Text(
            text = "Penggemar Seni",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Menu List
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Filled.Settings,
                    title = "Pengaturan",
                    onClick = { /* TODO */ }
                )
                HorizontalDivider(color = Color(0xFFF0EAE1))
                ProfileMenuItem(
                    icon = Icons.Filled.Info,
                    title = "Tentang Aplikasi",
                    onClick = onNavigateToAbout
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tombol Login/Logout
        Button(
            onClick = { /* TODO: Navigasi ke Login */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF994121)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Masuk / Daftar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(120.dp)) // padding bawah untuk bottom nav
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF994121),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A3B32),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Detail",
                tint = Color.Gray
            )
        }
    }
}
