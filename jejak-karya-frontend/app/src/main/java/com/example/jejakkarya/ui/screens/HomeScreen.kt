package com.example.jejakkarya.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jejakkarya.ui.components.AppNavigationDrawerContent
import com.example.jejakkarya.ui.components.BottomNavigationBar
import com.example.jejakkarya.ui.components.TopNavigationBar
import com.example.jejakkarya.ui.screens.tabs.BookmarkTab
import com.example.jejakkarya.ui.screens.tabs.ExploreTab
import com.example.jejakkarya.ui.viewmodel.GalleryViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: GalleryViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawerContent(
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    // Handle navigasi drawer di sini nanti
                }
            )
        }
    ) {
        Scaffold(
            topBar = { 
                TopNavigationBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchTriggered = { 
                        selectedTab = 0 // Pindah ke tab explore jika mencari
                        viewModel.fetchArtworks(searchQuery) 
                    },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                ) 
            },
            bottomBar = { 
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                ) 
            }
        ) { innerPadding ->
            if (selectedTab == 0) {
                ExploreTab(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    onArtworkClick = onNavigateToDetail
                )
            } else {
                BookmarkTab(
                    modifier = Modifier.padding(innerPadding),
                    onArtworkClick = onNavigateToDetail
                )
            }
        }
    }
}
