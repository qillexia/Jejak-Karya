package com.example.jejakkarya.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 25.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 12.dp),
            windowInsets = WindowInsets(0, 0, 0, 0)
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
            icon = { Icon(Icons.Filled.Search, contentDescription = "Cari") },
            label = { Text("Cari") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF994121),
                selectedTextColor = Color(0xFF994121),
                indicatorColor = Color(0xFFFFDBD0)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Koleksi") },
            label = { Text("Koleksi") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF994121),
                selectedTextColor = Color(0xFF994121),
                indicatorColor = Color(0xFFFFDBD0)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF994121),
                selectedTextColor = Color(0xFF994121),
                indicatorColor = Color(0xFFFFDBD0)
            )
        )
        }
    }
}
