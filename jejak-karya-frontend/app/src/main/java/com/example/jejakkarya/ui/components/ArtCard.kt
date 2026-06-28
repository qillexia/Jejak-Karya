package com.example.jejakkarya.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.jejakkarya.data.model.Artwork

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtCard(
    artwork: Artwork,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: ((Int) -> Unit)? = null,
    selectionMode: Boolean = false,
    isSelected: Boolean = false
) {
    // Animasi Scale ketika dipilih
    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "CardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
            .scale(cardScale)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { onClick(artwork.objectID) },
                onLongClick = { onLongClick?.invoke(artwork.objectID) }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 8.dp),
        // Warna Card menjadi sedikit abu agar tidak menyatu dengan background putih
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Gambar Karya Seni menggunakan Coil
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artwork.displayImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = artwork.displayTitle,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color(0xFF994121),
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.LightGray)
                )
                
                // Info Karya Seni
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = artwork.displayTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = artwork.displayArtist,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Overlay animasi jika card ini terpilih (menggunakan manual alpha untuk menghindari bug compiler)
            val overlayAlpha by animateFloatAsState(
                targetValue = if (isSelected) 0.25f else 0f,
                animationSpec = tween(200),
                label = "OverlayAlpha"
            )
            
            if (overlayAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF994121).copy(alpha = overlayAlpha))
                )
            }

            // Indikator Checklist yang muncul membesar
            if (selectionMode) {
                val checkScale by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                    label = "CheckScale"
                )
                
                if (checkScale > 0f) {
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .scale(checkScale)
                                .background(color = Color(0xFF994121), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
