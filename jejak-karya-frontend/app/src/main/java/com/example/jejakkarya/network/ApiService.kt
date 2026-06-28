package com.example.jejakkarya.network

import com.example.jejakkarya.data.model.SearchResponse
import com.example.jejakkarya.data.model.DetailResponse
import com.example.jejakkarya.data.model.BatchArtworksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    // 1. Mengambil daftar ID karya seni
    @GET("api")
    suspend fun searchArtworks(@Query("search") search: String? = null): SearchResponse
    
    // 2. Mengambil detail spesifik dari satu karya seni
    @GET("api/object/{id}")
    suspend fun getArtworkDetail(@Path("id") id: Int): DetailResponse
    
    // 3. Endpoint PINTAR: Backend mengurus pencarian + filter + pengembalian langsung N karya seni
    @GET("api/artworks")
    suspend fun getBatchArtworks(
        @Query("search") search: String,
        @Query("count") count: Int = 8,
        @Query("refresh") refresh: Boolean = false
    ): BatchArtworksResponse
}
