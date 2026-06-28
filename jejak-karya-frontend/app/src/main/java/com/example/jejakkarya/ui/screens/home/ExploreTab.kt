package com.example.jejakkarya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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

import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreTab(
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = viewModel(),
    searchQuery: String = "",
    onArtworkClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
        }
    }
    
    val categories = listOf("Semua", "Lukisan", "Patung", "Fotografi")
    var selectedCategory by rememberSaveable { mutableStateOf("Semua") }
    var showFooter by rememberSaveable { mutableStateOf(true) }
    
    // Optimasi Scroll & Animasi
    var targetDirection by rememberSaveable { mutableIntStateOf(1) }
    var categoryScrollIndex by rememberSaveable { mutableIntStateOf(0) }
    var categoryScrollOffset by rememberSaveable { mutableIntStateOf(0) }

    // Mempertahankan posisi scroll untuk tiap kategori secara persisten
    val gridStateSemua = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val gridStateLukisan = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val gridStatePatung = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val gridStateFotografi = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val searchGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()

    // Efek debounce pencarian realtime
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            kotlinx.coroutines.delay(800) // Tunggu 800ms sebelum nge-fetch
            viewModel.fetchArtworks(searchQuery)
        } else {
            // Jika kosong, kembalikan ke kategori saat ini
            val query = when (selectedCategory) {
                "Semua" -> "Sunflowers"
                "Lukisan" -> "Painting"
                "Patung" -> "Sculpture"
                "Fotografi" -> "Photography"
                else -> "Sunflowers"
            }
            viewModel.fetchArtworks(query)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            val query = when (selectedCategory) {
                "Semua" -> "Sunflowers"
                "Lukisan" -> "Painting"
                "Patung" -> "Sculpture"
                "Fotografi" -> "Photography"
                else -> "Sunflowers"
            }
            viewModel.refreshCurrentCategory(query)
        },
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (val state = uiState) {
            is GalleryState.Initial, is GalleryState.Loading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 120.dp),
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
                    Button(onClick = { 
                        val query = when (selectedCategory) {
                            "Semua" -> "Sunflowers"
                            "Lukisan" -> "Painting"
                            "Patung" -> "Sculpture"
                            "Fotografi" -> "Photography"
                            else -> "Sunflowers"
                        }
                        viewModel.fetchArtworks(query) 
                    }) {
                        Text("Coba Lagi")
                    }
                }
            }
            is GalleryState.Success -> {
                val featuredArtworks = if (searchQuery.isBlank()) state.artworks.take(3) else emptyList()
                // Jika tidak mencari, ambil 8 item setelah 3 item pertama (untuk grid). Jika mencari, ambil 10 item langsung.
                val gridArtworks = if (searchQuery.isBlank()) state.artworks.drop(3).take(8) else state.artworks.take(10)

                Crossfade(
                    targetState = if (searchQuery.isNotBlank()) "Search" else selectedCategory,
                    animationSpec = tween(400),
                    label = "CategoryTransition"
                ) { activeCategory ->
                    val currentGridState = when (activeCategory) {
                        "Search" -> searchGridState
                        "Semua" -> gridStateSemua
                        "Lukisan" -> gridStateLukisan
                        "Patung" -> gridStatePatung
                        "Fotografi" -> gridStateFotografi
                        else -> gridStateSemua
                    }

                    LazyVerticalGrid(
                        state = currentGridState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 120.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. Carousel Unggulan
                        if (featuredArtworks.isNotEmpty() && searchQuery.isBlank()) {
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

                        // 2. Kategori Chips (Hanya tampil jika tidak mencari)
                        if (searchQuery.isBlank()) {
                            item(span = { GridItemSpan(2) }) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Kategori Museum",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3B32),
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                                
                                // Memulihkan posisi scroll terakhir
                                val categoryState = rememberLazyListState(
                                    initialFirstVisibleItemIndex = categoryScrollIndex,
                                    initialFirstVisibleItemScrollOffset = categoryScrollOffset
                                )
                                
                                // Menyimpan posisi scroll secara real-time
                                LaunchedEffect(categoryState.firstVisibleItemIndex, categoryState.firstVisibleItemScrollOffset) {
                                    categoryScrollIndex = categoryState.firstVisibleItemIndex
                                    categoryScrollOffset = categoryState.firstVisibleItemScrollOffset
                                }

                                LazyRow(
                                    state = categoryState,
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
                                                    "Patung" -> "Sculpture"
                                                    "Fotografi" -> "Photography"
                                                    else -> "Sunflowers"
                                                }
                                                viewModel.fetchArtworks(query)
                                            },
                                            label = { Text(category, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF994121),
                                                selectedLabelColor = Color.White,
                                                containerColor = Color.White,
                                                labelColor = Color(0xFF4A3B32)
                                            ),
                                            elevation = FilterChipDefaults.filterChipElevation(elevation = 2.dp),
                                            shape = RoundedCornerShape(50)
                                        )
                                    }
                                }
                            }
                        }
                        }
                        
                        // 3. Grid Karya Seni Utama
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "Hasil Pencarian: $searchQuery" else "Koleksi Galeri",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3B32),
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                            )
                        }

                        items(gridArtworks, key = { it.objectID }) { artwork ->
                            ArtCard(
                                artwork = artwork,
                                onClick = { onArtworkClick(artwork.objectID) },
                                modifier = Modifier.animateItem() // Animate placement dan penambahan item baru secara instan
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
                    } // End of LazyVerticalGrid
                } // End of Crossfade
            }
        }
    }
}
