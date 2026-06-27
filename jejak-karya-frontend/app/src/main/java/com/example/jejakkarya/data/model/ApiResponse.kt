package com.example.jejakkarya.data.model

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val total: Int,
    val objectIDs: List<Int>?
)

data class DetailResponse(
    val success: Boolean,
    val data: Artwork
)
