package com.example.jejakkarya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jejakkarya.ui.components.ArtCard
import com.example.jejakkarya.ui.components.SkeletonArtCard
import com.example.jejakkarya.ui.viewmodel.GalleryState
import com.example.jejakkarya.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(
    viewModel: GalleryViewModel,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onArtworkClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0)) // Background sama dengan theme aplikasi
    ) {
        // Search Bar yang lebih clean dan modern
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { 
                Text(
                    text = "Cari seniman, karya, atau kategori...", 
                    fontSize = 15.sp, 
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            leadingIcon = { 
                Icon(Icons.Filled.Search, contentDescription = "Cari", tint = Color(0xFFC75A3A)) 
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        onSearchQueryChange("") 
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Hapus", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchTriggered() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF994121)
            ),
            shape = RoundedCornerShape(24.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Konten Hasil Pencarian
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            when {
                searchQuery.isEmpty() -> {
                    // Tampilan awal saat belum mencari
                    Column(
                        modifier = Modifier.fillMaxSize().padding(bottom = 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Apa yang ingin Anda jelajahi hari ini?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                uiState is GalleryState.Loading -> {
                    // Skeleton Loading
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(6) {
                            SkeletonArtCard()
                        }
                    }
                }
                
                uiState is GalleryState.Success -> {
                    val artworks = (uiState as GalleryState.Success).artworks
                    if (artworks.isEmpty()) {
                        // Empty State
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tidak ada karya seni yang ditemukan untuk '$searchQuery'",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Grid Karya Seni
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(artworks) { artwork ->
                                ArtCard(artwork = artwork, onClick = { onArtworkClick(artwork.objectID) })
                            }
                        }
                    }
                }
                
                uiState is GalleryState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as GalleryState.Error).message,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onSearchTriggered() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC75A3A))
                        ) {
                            Text("Coba Lagi", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
