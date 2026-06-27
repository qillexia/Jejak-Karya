package com.example.jejakkarya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jejakkarya.ui.screens.HomeScreen
import com.example.jejakkarya.ui.screens.detail.ArtworkDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Saat ini hanya ada rute "home" (karena login, splash, dll dilewati dulu)
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getInt("id") ?: return@composable
            ArtworkDetailScreen(
                artworkId = artworkId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
