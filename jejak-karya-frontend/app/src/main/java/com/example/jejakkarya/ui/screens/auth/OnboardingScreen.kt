package com.example.jejakkarya.ui.screens.auth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.jejakkarya.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onNavigateToHome: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    
    val pages = listOf(
        OnboardingPage(
            title = "Temukan Jejak Masa Lalu",
            description = "Telusuri ribuan situs sejarah dan budaya yang tersebar di seluruh Nusantara dengan cara yang modern.",
            imageRes = R.drawable.background1
        ),
        OnboardingPage(
            title = "Pelajari Budaya Leluhur",
            description = "Kenali lebih dekat tradisi dan warisan budaya yang tak ternilai harganya dari berbagai daerah.",
            imageRes = R.drawable.background2
        ),
        OnboardingPage(
            title = "Mulai Petualanganmu",
            description = "Jadilah bagian dari pelestari sejarah. Mari mulai perjalanan melintasi waktu sekarang juga.",
            imageRes = R.drawable.background3
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image (Full Screen)
        Image(
            painter = painterResource(id = R.drawable.background1),
            contentDescription = "Background",
            contentScale = ContentScale.Crop, // Agar gambar memenuhi layar
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay agar teks berwarna putih bisa terbaca jelas di atas gambar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.9f)
                        ),
                        startY = 0f
                    )
                )
        )

        // Teks Cursive di atas (Jejak Karya)
        Text(
            text = "Jejak Karya",
            color = Color.White,
            fontSize = 43.sp,
            fontFamily = FontFamily.Cursive, // Menggunakan font tulisan tangan bawaan
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )

        // Konten Bagian Bawah
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 30.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Horizontal Pager untuk menggeser halaman
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { position ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pages[position].title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pages[position].description,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Indikator Pager (Titik-titik)
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(pages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val color = if (isSelected) Color(0xFFC75A3A) else Color.Gray.copy(alpha = 0.5f)
                    val width = if (isSelected) 24.dp else 8.dp
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = width, height = 8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Lanjutkan
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1,
                                animationSpec = tween(durationMillis = 600)
                            )
                        }
                    } else {
                        onNavigateToHome()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC75A3A)), // Warna Orange/Bata
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjutkan",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
