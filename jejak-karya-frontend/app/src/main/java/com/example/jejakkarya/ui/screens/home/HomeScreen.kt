package com.example.jejakkarya.ui.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jejakkarya.ui.components.AppNavigationDrawerContent
import com.example.jejakkarya.ui.components.BottomNavigationBar
import com.example.jejakkarya.ui.components.ConfirmationModal
import com.example.jejakkarya.ui.components.TopNavigationBar
import com.example.jejakkarya.ui.screens.home.CollectionTab
import com.example.jejakkarya.ui.screens.home.ExploreTab
import com.example.jejakkarya.ui.viewmodel.GalleryViewModel
import com.example.jejakkarya.ui.viewmodel.BookmarkViewModel
import kotlinx.coroutines.launch

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    viewModel: GalleryViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val searchViewModel: GalleryViewModel = viewModel(key = "SearchViewModel")
    
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showSyncDialog by rememberSaveable { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawerContent(
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    if (route == "refresh") {
                        showSyncDialog = true
                    }
                    // Handle navigasi drawer lain di sini nanti
                }
            )
        }
    ) {
        Scaffold(
            topBar = { 
                TopNavigationBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                ) 
            },
            bottomBar = { } // Dikosongkan agar bisa float
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Crossfade(
                    targetState = selectedTab,
                    label = "TabCrossfade",
                    modifier = Modifier.fillMaxSize()
                ) { tab ->
                    when (tab) {
                        0 -> ExploreTab(
                            viewModel = viewModel,
                            searchQuery = "",
                            onArtworkClick = onNavigateToDetail
                        )
                        1 -> SearchTab(
                            viewModel = searchViewModel,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onSearchTriggered = { searchViewModel.fetchArtworks(searchQuery, count = 10) },
                            onArtworkClick = onNavigateToDetail
                        )
                        2 -> CollectionTab(
                            viewModel = bookmarkViewModel,
                            onArtworkClick = onNavigateToDetail
                        )
                        3 -> com.example.jejakkarya.ui.screens.home.ProfileTab(
                            onNavigateToAbout = onNavigateToAbout
                        )
                    }
                }
                
                // Floating Bottom Navigation Bar
                Box(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                ) {
                    BottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    ) 
                }
            }
        }
    }

    // Modal Konfirmasi Sinkronisasi
    ConfirmationModal(
        showDialog = showSyncDialog,
        title = "Sinkronisasi Data",
        message = "Apakah Anda yakin ingin menyinkronkan seluruh kategori karya seni? \n\n" +
                  "Aplikasi akan memeriksa semua kategori dan mengunduh mahakarya terbaru dari museum jika ada kategori yang kekurangan data.",
        confirmText = "Sinkronkan Sekarang",
        onConfirm = {
            showSyncDialog = false
            val currentQuery = if (searchQuery.isNotBlank()) searchQuery else "Sunflowers"
            // Force skeleton aktif agar skeleton loading muncul sambil memperbarui data
            viewModel.syncAllCategories(currentQuery, forceSkeleton = true)
        },
        onDismiss = { showSyncDialog = false }
    )
}
