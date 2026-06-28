package com.example.jejakkarya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import com.example.jejakkarya.ui.screens.home.HomeScreen
import com.example.jejakkarya.ui.screens.detail.ArtworkDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Scope ViewModel ke Activity agar cache datanya bisa dipakai bersama
    val galleryViewModel: com.example.jejakkarya.ui.viewmodel.GalleryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val bookmarkViewModel: com.example.jejakkarya.ui.viewmodel.BookmarkViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Saat ini hanya ada rute "home" (karena login, splash, dll dilewati dulu)
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = galleryViewModel,
                bookmarkViewModel = bookmarkViewModel,
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                }
            )
        }
        
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getInt("id") ?: return@composable
            ArtworkDetailScreen(
                artworkId = artworkId,
                onNavigateBack = { navController.popBackStack() },
                bookmarkViewModel = bookmarkViewModel,
                galleryViewModel = galleryViewModel
            )
        }
        
        composable(
            route = "about",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            com.example.jejakkarya.ui.screens.about.AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
