package com.example.jejakkarya.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jejakkarya.data.model.Artwork
import com.example.jejakkarya.data.local.ArtworkEntity
import com.example.jejakkarya.network.RetrofitClient
import com.example.jejakkarya.ui.viewmodel.BookmarkViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkDetailScreen(
    artworkId: Int,
    onNavigateBack: () -> Unit,
    bookmarkViewModel: BookmarkViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var artwork by remember { mutableStateOf<Artwork?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isSavedFlow = remember<Flow<Boolean>>(artworkId) { bookmarkViewModel.isSaved(artworkId) }
    val isSaved by isSavedFlow.collectAsState(initial = false)

    LaunchedEffect(artworkId) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.instance.getArtworkDetail(artworkId)
                if (response.success) {
                    artwork = response.data
                } else {
                    errorMessage = "Gagal memuat detail karya seni."
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan koneksi saat memuat detail."
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Karya") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (artwork != null) {
                FloatingActionButton(
                    onClick = {
                        if (isSaved) {
                            bookmarkViewModel.removeArtwork(artworkId)
                        } else {
                            val entity = ArtworkEntity(
                                objectID = artwork!!.objectID,
                                title = artwork!!.displayTitle,
                                origin = artwork!!.displayOrigin,
                                medium = artwork!!.displayMedium,
                                displayImage = artwork!!.displayImage
                            )
                            bookmarkViewModel.saveArtwork(entity)
                        }
                    },
                    containerColor = if (isSaved) Color(0xFF994121) else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isSaved) "Hapus dari Koleksi" else "Simpan ke Koleksi",
                        tint = if (isSaved) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            } else if (artwork != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = artwork!!.displayImage,
                        contentDescription = artwork!!.displayTitle,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = artwork!!.displayTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Asal: ${artwork!!.displayOrigin}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Medium: ${artwork!!.displayMedium}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Deskripsi atau informasi tambahan bisa diletakkan di sini jika ada dari API
                    }
                }
            }
        }
    }
}
