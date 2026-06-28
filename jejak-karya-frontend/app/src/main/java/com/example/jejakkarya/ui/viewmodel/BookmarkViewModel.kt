package com.example.jejakkarya.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jejakkarya.data.local.ArtworkEntity
import com.example.jejakkarya.data.local.StorageHelper
import com.example.jejakkarya.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BookmarkRepository

    private val _isRefreshing = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        val storageHelper = StorageHelper(application)
        repository = BookmarkRepository(storageHelper)
    }

    // List koleksi yang otomatis terupdate saat database berubah
    val savedArtworks: StateFlow<List<ArtworkEntity>> = repository.getAllSavedArtworks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isSaved(id: Int): Flow<Boolean> {
        return repository.isArtworkSaved(id)
    }
    
    fun saveArtwork(artwork: ArtworkEntity) {
        viewModelScope.launch {
            repository.saveArtwork(artwork)
        }
    }

    fun removeArtwork(id: Int) {
        viewModelScope.launch {
            repository.removeArtwork(id)
        }
    }
    
    fun removeArtworks(ids: List<Int>) {
        viewModelScope.launch {
            repository.removeArtworks(ids)
        }
    }

    fun refreshCollection() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Memaksa StateFlow untuk memancarkan ulang data terbaru dari storage
            kotlinx.coroutines.delay(500)
            _isRefreshing.value = false
        }
    }
}
