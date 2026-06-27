package com.example.jejakkarya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }

    if (isSearchActive) {
        TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Cari artefak...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearchTriggered()
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        focusedIndicatorColor = Color(0xFF4A3B32),
                        unfocusedIndicatorColor = Color(0xFF4A3B32).copy(alpha = 0.5f)
                    )
                )
            },
            actions = {
                IconButton(onClick = { 
                    isSearchActive = false 
                    onSearchTriggered()
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Color(0xFF4A3B32))
                }
                IconButton(onClick = onSearchTriggered) {
                    Icon(Icons.Filled.Search, contentDescription = "Cari", tint = Color(0xFF4A3B32))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFFFFFF)
            )
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Jejak Karya",
                    fontWeight = FontWeight(1000),
                    fontFamily = FontFamily.Cursive,
                    color = Color(0xFF4A3B32),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = Color(0xFF4A3B32),
                        modifier = Modifier.padding(start = 18.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { isSearchActive = true }) {
                    Icon(Icons.Filled.Search, contentDescription = "Cari", tint = Color(0xFF4A3B32))
                }
                
                // Profil A
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp, start = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC75A3A))
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFFFFFFF)
            )
        )
    }
}
