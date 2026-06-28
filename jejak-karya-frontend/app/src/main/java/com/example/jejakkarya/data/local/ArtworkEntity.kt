package com.example.jejakkarya.data.local

data class ArtworkEntity(
    val objectID: Int,
    val title: String,
    val origin: String,
    val medium: String,
    val displayImage: String,
    val artistDisplayName: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)
