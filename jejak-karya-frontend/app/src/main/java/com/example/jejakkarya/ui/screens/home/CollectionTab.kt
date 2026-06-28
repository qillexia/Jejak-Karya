package com.example.jejakkarya.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jejakkarya.data.model.Artwork
import com.example.jejakkarya.ui.components.ArtCard
import com.example.jejakkarya.ui.components.ConfirmationModal
import com.example.jejakkarya.ui.viewmodel.BookmarkViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTab(
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val savedArtworks by viewModel.savedArtworks.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // State Multi-Select
    val selectedItems = remember { mutableStateListOf<Int>() }
    val isSelectionMode = selectedItems.isNotEmpty()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshCollection() },
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                if (savedArtworks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 160.dp)
                        ) {
                            Text(
                                text = "Koleksi Kosong",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF994121)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Anda belum menyimpan karya seni apapun.\nKlik ikon hati pada detail karya untuk menyimpannya.",
                                color = Color(0xFF4A3B32),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 8.dp, top = if (isSelectionMode) 88.dp else 8.dp, end = 8.dp, bottom = 120.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(savedArtworks, key = { it.objectID }) { entity ->
                            val artwork = Artwork(
                                objectID = entity.objectID,
                                title = entity.title,
                                culture = entity.origin,
                                country = null,
                                medium = entity.medium,
                                primaryImageSmall = entity.displayImage,
                                primaryImage = null,
                                artistDisplayName = entity.artistDisplayName
                            )
                            
                            val isSelected = selectedItems.contains(entity.objectID)
                            
                            ArtCard(
                                artwork = artwork,
                                selectionMode = isSelectionMode,
                                isSelected = isSelected,
                                onLongClick = { id ->
                                    if (!isSelectionMode) {
                                        selectedItems.add(id)
                                    }
                                },
                                onClick = { id ->
                                    if (isSelectionMode) {
                                        if (selectedItems.contains(id)) {
                                            selectedItems.remove(id)
                                        } else {
                                            selectedItems.add(id)
                                        }
                                    } else {
                                        onArtworkClick(id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Contextual Action Bar dengan Animasi
        androidx.compose.animation.AnimatedVisibility(
            visible = isSelectionMode,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { -it }) + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }) + androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(64.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF994121), contentColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedItems.clear() }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Batal", tint = Color.White)
                    }
                    
                    Text(
                        text = "${selectedItems.size} Terpilih",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                    
                    IconButton(
                        onClick = {
                            if (selectedItems.size == savedArtworks.size) {
                                selectedItems.clear()
                            } else {
                                selectedItems.clear()
                                selectedItems.addAll(savedArtworks.map { it.objectID })
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.DoneAll, contentDescription = "Pilih Semua", tint = Color.White)
                    }
                    
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.White)
                    }
                }
            }
        }
    }
    // Modal Konfirmasi Hapus Koleksi
    ConfirmationModal(
        showDialog = showDeleteDialog,
        title = "Hapus Karya Seni",
        message = "Apakah Anda yakin ingin menghapus ${selectedItems.size} karya seni terpilih dari daftar koleksi Anda?",
        confirmText = "Ya, Hapus",
        onConfirm = {
            viewModel.removeArtworks(selectedItems.toList())
            selectedItems.clear()
            showDeleteDialog = false
        },
        onDismiss = { showDeleteDialog = false }
    )
}
