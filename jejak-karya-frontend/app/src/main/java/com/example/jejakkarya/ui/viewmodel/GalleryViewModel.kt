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
        fetchArtworks()
    }

    fun fetchArtworks(query: String = "Sunflowers", isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            // 1. Cek In-Memory Cache (RAM)
            if (!isRefresh && queryCache.containsKey(query)) {
                _uiState.value = GalleryState.Success(queryCache[query]!!)
                return@launch
            }

            // 2. Cek Offline Cache (SharedPreferences)
            val offlineCache = storageHelper.getHomeCache(query)
            if (!isRefresh && offlineCache.isNotEmpty()) {
                val cachedArtworks = offlineCache.map { entity ->
                    Artwork(
                        objectID = entity.objectID,
                        title = entity.title,
                        culture = entity.origin,
                        country = null,
                        medium = entity.medium,
                        primaryImageSmall = entity.displayImage,
                        primaryImage = null
                    )
                }
                queryCache[query] = cachedArtworks
                _uiState.value = GalleryState.Success(cachedArtworks)
                return@launch
            }

            if (!isRefresh) {
                _uiState.value = GalleryState.Initial
            }
            try {
                // Beri batas waktu maksimal 15 detik untuk keseluruhan proses
                withTimeoutOrNull(15000L) {
                    val searchJob = async {
                        // 1. Ambil daftar ID dari API (/api)
                        val searchResponse = RetrofitClient.instance.searchArtworks(query)
                        
                        if (searchResponse.success && searchResponse.data.objectIDs != null) {
                            val allIds = searchResponse.data.objectIDs
                            // Tingkatkan batas penarikan awal ke 20, untuk menggaransi kita mendapat minimal 11 gambar (3 carousel + 8 grid) setelah filtering gambar kosong.
                            val targetIds = allIds.take(20)
                            
                            // Siapkan list kosong yang akan diisi secara progresif
                            val loadedArtworks = mutableListOf<Artwork>()
                            
                            // 2. Fetch detail dari masing-masing ID secara paralel
                            val deferredArtworks = targetIds.map { id ->
                                async {
                                    try {
                                        val detailResp = RetrofitClient.instance.getArtworkDetail(id)
                                        if (detailResp.success && detailResp.data.displayImage.isNotEmpty()) {
                                            // Begitu satu gambar selesai dimuat, langsung kirim ke layar UI!
                                            synchronized(loadedArtworks) {
                                                loadedArtworks.add(detailResp.data)
                                                // Ubah state dari Loading/Initial menjadi Success secara bertahap
                                                _uiState.value = GalleryState.Success(loadedArtworks.toList())
                                            }
                                        }
                                    } catch (e: Exception) {
                                        // Abaikan item yang gagal dimuat
                                    }
                                }
                            }
                            
                            // Tunggu semua selesai untuk memastikan tidak ada yang tersisa
                            deferredArtworks.awaitAll()
                            
                            if (loadedArtworks.isEmpty()) {
                                if (!isRefresh) _uiState.value = GalleryState.Error("Tidak ada gambar artefak yang tersedia saat ini.")
                            } else {
                                // 2. Simpan hasil akhir ke dalam cache in-memory
                                queryCache[query] = loadedArtworks.toList()
                                
                                // 3. Simpan ke Offline Cache (SharedPreferences)
                                val entitiesToSave = loadedArtworks.map { artwork ->
                                    ArtworkEntity(
                                        objectID = artwork.objectID,
                                        title = artwork.title,
                                        origin = artwork.culture ?: "",
                                        medium = artwork.medium ?: "",
                                        displayImage = artwork.primaryImageSmall ?: ""
                                    )
                                }
                                storageHelper.saveHomeCache(query, entitiesToSave)
                                
                                _uiState.value = GalleryState.Success(loadedArtworks.toList())
                            }
                        } else {
                            if (!isRefresh) _uiState.value = GalleryState.Error("Data karya seni tidak ditemukan.")
                        }
                    }

                    // Tahan nafas selama 300ms. Jika internet sangat cepat dan searchJob selesai (atau minimal dapat 1 data),
                    // state akan berubah menjadi Success. 
                    // Jika setelah 300ms masih Initial (belum dapat apa-apa), baru kita tampilkan Skeleton Loading.
                    kotlinx.coroutines.delay(300)
                    if (searchJob.isActive && _uiState.value == GalleryState.Initial) {
                        _uiState.value = GalleryState.Loading
                    }
                    
                    searchJob.await()
                } ?: run {
                    if (!isRefresh) _uiState.value = GalleryState.Error("Permintaan kehabisan waktu (Timeout). Pastikan koneksi internet Anda stabil.")
                }
            } catch (e: Exception) {
                if (!isRefresh) _uiState.value = GalleryState.Error("Terjadi kesalahan jaringan: ${e.localizedMessage}")
            } finally {
                if (isRefresh) {
                    _isRefreshing.value = false
                }
            }
        }
    }
}
