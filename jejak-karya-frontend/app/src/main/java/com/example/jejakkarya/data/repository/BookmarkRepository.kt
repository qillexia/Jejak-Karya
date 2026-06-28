package com.example.jejakkarya.data.repository

import com.example.jejakkarya.data.local.ArtworkEntity
import com.example.jejakkarya.data.local.StorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkRepository(private val storageHelper: StorageHelper) {
    
    fun getAllSavedArtworks(): Flow<List<ArtworkEntity>> {
        return storageHelper.bookmarksFlow
    }
    
    fun isArtworkSaved(id: Int): Flow<Boolean> {
        return storageHelper.bookmarksFlow.map { list -> list.any { it.objectID == id } }
    }
    
    fun saveArtwork(artwork: ArtworkEntity) {
        storageHelper.saveBookmark(artwork)
    }
    
    fun removeArtwork(id: Int) {
        storageHelper.removeBookmark(id)
    }
    
    fun removeArtworks(ids: List<Int>) {
        storageHelper.removeBookmarks(ids)
    }
}
