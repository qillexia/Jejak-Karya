package com.example.jejakkarya.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.jejakkarya.ui.viewmodel.BookmarkViewModel

@Composable
fun BookmarkTab(
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val savedArtworks by viewModel.savedArtworks.collectAsState()

    if (savedArtworks.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            contentPadding = PaddingValues(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(savedArtworks) { entity ->
                // Konversi sementara dari Entity ke Model untuk dipakai di ArtCard
                val artwork = Artwork(
                    objectID = entity.objectID,
                    title = entity.title,
                    culture = entity.origin,
                    country = null,
                    medium = entity.medium,
                    primaryImageSmall = entity.displayImage,
                    primaryImage = null
                )
                ArtCard(
                    artwork = artwork,
                    onClick = { onArtworkClick(entity.objectID) }
                )
            }
        }
    }
}
