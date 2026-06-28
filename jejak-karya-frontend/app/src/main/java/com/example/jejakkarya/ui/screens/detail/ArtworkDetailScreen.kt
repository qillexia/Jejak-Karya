package com.example.jejakkarya.ui.screens.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.jejakkarya.data.local.ArtworkEntity
import com.example.jejakkarya.data.model.Artwork
import com.example.jejakkarya.network.RetrofitClient
import com.example.jejakkarya.ui.viewmodel.BookmarkViewModel
import com.example.jejakkarya.ui.viewmodel.GalleryViewModel
import com.example.jejakkarya.ui.viewmodel.GalleryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun ArtworkDetailScreen(
    artworkId: Int,
    onNavigateBack: () -> Unit,
    bookmarkViewModel: BookmarkViewModel = viewModel(),
    galleryViewModel: GalleryViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Coba cari data dari cache GalleryViewModel yang sudah diunduh di HomeScreen
    val cachedArtwork = remember(artworkId) {
        val state = galleryViewModel.uiState.value
        if (state is GalleryState.Success) {
            state.artworks.find { it.objectID == artworkId }
        } else null
    }
    
    var artwork by remember { mutableStateOf<Artwork?>(cachedArtwork) }
    // Jika data ditemukan di cache, loading langsung selesai (instan!)
    var isLoading by remember { mutableStateOf(cachedArtwork == null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isSavedFlow = remember<Flow<Boolean>>(artworkId) { bookmarkViewModel.isSaved(artworkId) }
    val isSaved by isSavedFlow.collectAsState(initial = false)

    // Animasi Detak Jantung (Heart Beat)
    val heartScale by animateFloatAsState(
        targetValue = if (isSaved) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "HeartScale"
    )

    LaunchedEffect(artworkId) {
        // Hanya panggil API jika datanya TIDAK ADA di cache (misalnya saat Deep Linking)
        if (cachedArtwork == null) {
            coroutineScope.launch {
                try {
                    val response = RetrofitClient.instance.getArtworkDetail(artworkId)
                    if (response.success) {
                        artwork = response.data
                    } else {
                        errorMessage = "Gagal memuat detail karya seni."
                    }
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan jaringan."
                } finally {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF994121))
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    } else {
        val scrollState = rememberScrollState()
        
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F2EF))) {
            // 1. Gambar Parallax-style
            val imageOffset = (scrollState.value * 0.4f).coerceAtMost(300f)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .graphicsLayer { translationY = -imageOffset }
            ) {
                AsyncImage(
                    model = artwork?.displayImage,
                    contentDescription = artwork?.displayTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                                startY = 250f
                            )
                        )
                )
            }

            // 2. Konten Scrollable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(340.dp))
                
                // Lembaran Konten Putih dengan Radius
                Card(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 500.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 28.dp)
                    ) {
                        // Header: Judul
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = artwork?.displayTitle ?: "Tanpa Judul",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4A3B32),
                                lineHeight = 30.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Seniman / Artist Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Seniman",
                                tint = Color(0xFF994121),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = artwork?.displayArtist ?: "Seniman Tidak Diketahui",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF994121)
                            )
                        }
                        
                        if (artwork?.artistDisplayBio?.isNotBlank() == true) {
                            Text(
                                text = artwork?.artistDisplayBio ?: "",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Deskripsi Dinamis
                        Text(
                            text = "Tentang Karya",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3B32),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = artwork?.displayDescription ?: "Tidak ada deskripsi.",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Grid Data Detail (2x2)
                        Text(
                            text = "Spesifikasi Karya",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3B32),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DetailItemBox(icon = Icons.Filled.DateRange, label = "Periode / Tahun", value = artwork?.displayDate ?: "-", modifier = Modifier.weight(1f).padding(end = 8.dp))
                                DetailItemBox(icon = Icons.Filled.LocationOn, label = "Asal / Budaya", value = artwork?.displayOrigin ?: "-", modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DetailItemBox(icon = Icons.Filled.Build, label = "Material", value = artwork?.displayMedium ?: "-", modifier = Modifier.weight(1f).padding(end = 8.dp))
                                DetailItemBox(icon = Icons.Filled.Info, label = "Klasifikasi", value = artwork?.classification?.takeIf { it.isNotBlank() } ?: "-", modifier = Modifier.weight(1f))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Informasi Tambahan
                        Text(
                            text = "Kredit & Repositori",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3B32),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val creditLine = artwork?.creditLine?.takeIf { it.isNotBlank() } ?: "-"
                        val repository = artwork?.repository?.takeIf { it.isNotBlank() } ?: "The Metropolitan Museum of Art"
                        
                        Text(text = "Repositori: $repository", fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Kredit: $creditLine", fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
                        
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            // 3. Tombol Kembali di Pojok Kiri Atas
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 48.dp, start = 16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }

            // 4. Tombol Favorit Mengambang di Pojok Kanan Atas
            IconButton(
                onClick = {
                    if (isSaved) {
                        bookmarkViewModel.removeArtwork(artworkId)
                    } else {
                        artwork?.let {
                            bookmarkViewModel.saveArtwork(
                                ArtworkEntity(
                                    objectID = it.objectID,
                                    title = it.displayTitle,
                                    origin = it.displayOrigin,
                                    medium = it.displayMedium,
                                    displayImage = it.displayImage,
                                    artistDisplayName = it.displayArtist
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Simpan",
                    tint = if (isSaved) Color(0xFFFF5252) else Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(
                            scaleX = heartScale,
                            scaleY = heartScale
                        )
                )
            }
        }
    }
}

@Composable
fun DetailItemBox(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        // Lingkaran background untuk icon agar posisinya super simetris dan rapi
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFFFF6F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF994121),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A3B32),
                lineHeight = 18.sp
            )
        }
    }
}
