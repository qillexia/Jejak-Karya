package com.example.jejakkarya.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.jejakkarya.ui.components.ArtCard
import com.example.jejakkarya.ui.components.SkeletonArtCard
import com.example.jejakkarya.ui.viewmodel.GalleryState
import com.example.jejakkarya.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreTab(
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val categories = listOf("Semua", "Lukisan", "Mesir Kuno", "Patung", "Senjata")
    var selectedCategory by remember { mutableStateOf("Semua") }
    var showFooter by remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is GalleryState.Initial -> {
                Box(modifier = Modifier.fillMaxSize())
            }
            is GalleryState.Loading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Skeleton
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        )
                    }
                    
                    // Chips Skeleton
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(4) {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.LightGray.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    items(6) {
                        SkeletonArtCard()
                    }
                }
            }
            is GalleryState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchArtworks() }) {
                        Text("Coba Lagi")
                    }
                }
            }
            is GalleryState.Success -> {
                val featuredArtworks = state.artworks.take(3)
                // Membatasi tepat 8 card (agar grid genap 4 baris x 2 kolom)
                val gridArtworks = state.artworks.drop(3).take(8)

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.fetchArtworks(selectedCategory, isRefresh = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                    // 1. Carousel Unggulan (Hanya jika ada data unggulan)
                    if (featuredArtworks.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Karya Unggulan Pilihan",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3B32),
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(featuredArtworks) { artwork ->
                                        Card(
                                            modifier = Modifier
                                                .width(280.dp)
                                                .height(160.dp)
                                                .clickable { onArtworkClick(artwork.objectID) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                AsyncImage(
                                                    model = artwork.displayImage,
                                                    contentDescription = artwork.displayTitle,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                // Dark gradient overlay agar teks terbaca jelas
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                                                startY = 100f
                                                            )
                                                        )
                                                )
                                                Text(
                                                    text = artwork.displayTitle,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Kategori Chips
                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "Kategori Museum",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3B32),
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    val isSelected = selectedCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategory = category
                                            val query = when (category) {
                                                "Semua" -> "Sunflowers"
                                                "Lukisan" -> "Painting"
                                                "Mesir Kuno" -> "Egypt"
                                                "Patung" -> "Sculpture"
                                                "Senjata" -> "Weapon"
                                                else -> "Sunflowers"
                                            }
                                            viewModel.fetchArtworks(query)
                                        },
                                        label = { Text(category) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF994121),
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.White,
                                            labelColor = Color(0xFF4A3B32)
                                        ),
                                        elevation = FilterChipDefaults.filterChipElevation(elevation = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Grid Karya Seni Utama
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Koleksi Galeri",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3B32),
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }

                    items(gridArtworks) { artwork ->
                        ArtCard(
                            artwork = artwork,
                            onClick = { id -> onArtworkClick(id) }
                        )
                    }

                    // 4. Footer Card di bagian paling bawah (Dapat Ditutup)
                    if (showFooter) {
                        item(span = { GridItemSpan(2) }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6F3)), // Warna oranye super pudar
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Tidak menemukan yang Anda cari?",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF994121),
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Gunakan fitur pencarian di atas untuk menelusuri jutaan koleksi mahakarya lainnya dari berbagai penjuru dunia.",
                                            color = Color(0xFF4A3B32),
                                            fontSize = 14.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                    
                                    // Tombol Silang (Close) di Pojok Kanan Atas
                                    IconButton(
                                        onClick = { showFooter = false },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Tutup Footer",
                                            tint = Color(0xFF994121)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                } // End of PullToRefreshBox
            }
        }
    }
}
