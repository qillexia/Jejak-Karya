package com.example.jejakkarya.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable
import com.example.jejakkarya.R
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Tentang Aplikasi",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3B32)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF994121))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF9F6F0)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F6F0))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logo / Judul
            Image(
                painter = painterResource(id = R.drawable.jejak_karuhun),
                contentDescription = "Logo Jejak Karya",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Jejak Karya",
                fontSize = 36.sp,
                fontWeight = FontWeight(1000),
                fontFamily = FontFamily.Cursive,
                color = Color(0xFF4A3B32)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Deskripsi
            Text(
                text = "Jejak Karya adalah aplikasi galeri seni interaktif eksklusif yang menyajikan mahakarya dunia langsung ke dalam genggaman Anda, dirancang khusus bagi para pecinta seni untuk mengeksplorasi ribuan karya legendaris tanpa batas ruang dan waktu, menyelami kekayaan sejarah peradaban dari berbagai belahan dunia, serta memberikan Anda kebebasan penuh untuk mengumpulkan inspirasi dan membangun koleksi galeri seni impian Anda secara personal dan elegan.",
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 15.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Dibuat Oleh
            Text(
                text = "Dibuat oleh :",
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            // Social Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Instagram Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { uriHandler.openUri("https://www.instagram.com/haqilabd") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.instagram),
                        contentDescription = "Instagram",
                        modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("@haqilabd", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A3B32))
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Github Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { uriHandler.openUri("https://github.com/qillexia") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("@qillexia", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A3B32))
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Credit
            Text(
                text = "Sumber Data & API",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A3B32)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Data dan gambar bersumber dari The Metropolitan Museum of Art melalui Collection API publik.",
                fontSize = 12.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "© 2026 Jejak Karya. Hak Cipta Dilindungi.",
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}
