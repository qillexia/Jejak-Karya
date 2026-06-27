package com.example.jejakkarya.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFFFFFFFF)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Explore") },
            label = { Text("Eksplorasi") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF994121),
                selectedTextColor = Color(0xFF994121),
                indicatorColor = Color(0xFFFFDBD0)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Koleksi") },
            label = { Text("Koleksi") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF994121),
                selectedTextColor = Color(0xFF994121),
                indicatorColor = Color(0xFFFFDBD0)
            )
        )
    }
}
