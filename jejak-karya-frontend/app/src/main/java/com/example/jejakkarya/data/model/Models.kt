package com.example.jejakkarya.data.model

data class Artwork(
    val objectID: Int,
    val title: String?,
    val culture: String?,
    val country: String?,
    val medium: String?,
    val primaryImageSmall: String?,
    val primaryImage: String?
) {
    // Helper properties untuk langsung dipakai di UI tanpa pengecekan null yang rumit
    val displayOrigin: String
        get() = culture ?: country ?: "Tidak diketahui"
        
    val displayMedium: String
        get() = medium ?: "Tidak diketahui"
        
    val displayTitle: String
        get() = if (title.isNullOrEmpty()) "Tanpa Judul" else title
        
    val displayImage: String
        get() = primaryImageSmall?.takeIf { it.isNotEmpty() } 
            ?: primaryImage?.takeIf { it.isNotEmpty() } 
            ?: ""
}
