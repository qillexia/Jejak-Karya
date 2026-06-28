package com.example.jejakkarya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.jejakkarya.ui.screens.home.HomeScreen
import com.example.jejakkarya.ui.screens.detail.ArtworkDetailScreen
import com.example.jejakkarya.ui.screens.auth.LoginScreen
import com.example.jejakkarya.ui.screens.auth.OnboardingScreen
import com.example.jejakkarya.ui.screens.auth.SplashScreen
import com.example.jejakkarya.ui.screens.settings.SettingsScreen
import com.example.jejakkarya.ui.screens.settings.EditProfileScreen

@Composable
fun AppNavigation(
    settingsViewModel: com.example.jejakkarya.ui.viewmodel.SettingsViewModel,
    authViewModel: com.example.jejakkarya.ui.viewmodel.AuthViewModel,
    startDestination: String
) {
    val navController = rememberNavController()
    // Scope ViewModel ke Activity agar cache datanya bisa dipakai bersama
    val galleryViewModel: com.example.jejakkarya.ui.viewmodel.GalleryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val bookmarkViewModel: com.example.jejakkarya.ui.viewmodel.BookmarkViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- Auth Flow ---
        composable(
            route = "splash",
            enterTransition = { fadeIn(animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(250)) }
        ) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } } }
            )
        }

        composable(
            route = "onboarding",
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) }
        ) {
            OnboardingScreen(
                onNavigateToHome = { navController.navigate("login") { popUpTo("onboarding") { inclusive = true } } }
            )
        }

        composable(
            route = "login",
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) }
        ) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToHome = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable(
            route = "register",
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(600)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(600)) }
        ) {
            com.example.jejakkarya.ui.screens.auth.RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToHome = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // --- Main Flow ---
        composable("home") {
            HomeScreen(
                viewModel = galleryViewModel,
                bookmarkViewModel = bookmarkViewModel,
                authViewModel = authViewModel,
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onLogout = { 
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } } 
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

        composable(
            route = "settings",
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
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = settingsViewModel,
                onEditProfile = {
                    navController.navigate("edit_profile")
                },
                onLogout = { 
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo("splash") { inclusive = true } } 
                }
            )
        }

        composable(
            route = "edit_profile",
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
        ) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }
    }
}
