package com.example.jejakkarya.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jejakkarya.data.local.ArtworkEntity
import com.example.jejakkarya.data.local.StorageHelper
import com.example.jejakkarya.data.model.Artwork
import com.example.jejakkarya.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

sealed class GalleryState {
    object Initial : GalleryState()
    object Loading : GalleryState()
    data class Success(val artworks: List<Artwork>) : GalleryState()
    data class Error(val message: String) : GalleryState()
}

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val storageHelper = StorageHelper(application)
    private val _uiState = MutableStateFlow<GalleryState>(GalleryState.Initial)
    val uiState: StateFlow<GalleryState> = _uiState

    // State untuk pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Cache in-memory untuk menyimpan hasil pencarian per kategori
    private val queryCache = mutableMapOf<String, List<Artwork>>()
        
    init {
        fetchArtworks("Sunflowers")
    }

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    fun fetchArtworks(query: String, isRefresh: Boolean = false, updateUi: Boolean = true, showSkeleton: Boolean = true, count: Int = 11): kotlinx.coroutines.Job {
        return viewModelScope.launch {
            if (!isRefresh && updateUi) {
                // Cek cache in-memory dulu
                queryCache[query]?.let { cachedArtworks ->
                    _uiState.value = GalleryState.Success(cachedArtworks)
                    return@launch
                }
                
                // Cek Offline Cache
                val offlineCache = storageHelper.getHomeCache(query)
                if (offlineCache.isNotEmpty()) {
                    val cachedArtworks = offlineCache.map { entity ->
                        Artwork(
                            objectID = entity.objectID,
                            title = entity.title,
                            culture = entity.origin,
                            country = null,
                            medium = entity.medium,
                            primaryImageSmall = entity.displayImage,
                            primaryImage = null,
                            artistDisplayName = entity.artistDisplayName
                        )
                    }
                    queryCache[query] = cachedArtworks
                    _uiState.value = GalleryState.Success(cachedArtworks)
                    return@launch
                }
            }

            if (updateUi && showSkeleton) {
                _uiState.value = GalleryState.Loading
            }
            try {
                // STRATEGI BARU: 1 panggilan ke Backend, Backend yang urus semuanya!
                val response = RetrofitClient.instance.getBatchArtworks(
                    search = query,
                    count = count,
                    refresh = isRefresh
                )
                
                if (response.success && response.data.isNotEmpty()) {
                    // Urutkan agar karya dengan "Seniman Tidak Diketahui" selalu berada di paling bawah
                    val artworks = response.data.sortedBy { 
                        if (it.displayArtist == "Seniman Tidak Diketahui") 1 else 0 
                    }
                    
                    // Simpan ke cache in-memory
                    queryCache[query] = artworks
                    
                    // Simpan ke Offline Cache (SharedPreferences)
                    val entitiesToSave = artworks.map { artwork ->
                        ArtworkEntity(
                            objectID = artwork.objectID,
                            title = artwork.displayTitle,
                            origin = artwork.displayOrigin,
                            medium = artwork.displayMedium,
                            displayImage = artwork.displayImage,
                            artistDisplayName = artwork.displayArtist
                        )
                    }
                    storageHelper.saveHomeCache(query, entitiesToSave)
                    
                    if (updateUi) _uiState.value = GalleryState.Success(artworks)
                } else {
                    if (!isRefresh && updateUi) _uiState.value = GalleryState.Error("Data karya seni tidak ditemukan.")
                }
            } catch (e: Exception) {
                if (!isRefresh && updateUi) {
                    _uiState.value = GalleryState.Error("Gagal terhubung ke server. Pastikan server aktif.")
                } else if (isRefresh && updateUi) {
                    _toastMessage.emit("Gagal sinkronisasi. Server mungkin sedang offline.")
                    // Kembalikan ke cache jika sebelumnya menampilkan skeleton
                    if (showSkeleton) {
                        queryCache[query]?.let { cachedArtworks ->
                            _uiState.value = GalleryState.Success(cachedArtworks)
                        } ?: run {
                            _uiState.value = GalleryState.Error("Gagal terhubung ke server. Pastikan server aktif.")
                        }
                    }
                }
            } finally {
                if (isRefresh && updateUi) {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun syncAllCategories(currentActiveQuery: String, forceSkeleton: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true
            
            // Prioritaskan kategori yang sedang aktif dulu
            val activeJob = fetchArtworks(
                query = currentActiveQuery, 
                isRefresh = true, 
                updateUi = true, 
                showSkeleton = forceSkeleton
            )
            
            // Tunggu fetch selesai agar spinner loading tidak menghilang sebelum waktunya
            activeJob.join()
            
            // Matikan indikator pull-to-refresh setelah kategori utama selesai
            _isRefreshing.value = false
            
            // Lanjutkan sinkronisasi kategori lainnya secara berurutan (jeda 1 detik) 
            // untuk mencegah API Rate Limit (403 Forbidden) dari The Met Museum
            val otherQueries = listOf("Sunflowers", "Painting", "Sculpture", "Photography").filter { it != currentActiveQuery }
            for (query in otherQueries) {
                kotlinx.coroutines.delay(1000)
                fetchArtworks(
                    query = query, 
                    isRefresh = true, 
                    updateUi = false, 
                    showSkeleton = false
                )
            }
        }
    }

    fun refreshCurrentCategory(query: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val activeJob = fetchArtworks(
                query = query, 
                isRefresh = true, 
                updateUi = true, 
                showSkeleton = true
            )
            activeJob.join()
            _isRefreshing.value = false
        }
    }
}
