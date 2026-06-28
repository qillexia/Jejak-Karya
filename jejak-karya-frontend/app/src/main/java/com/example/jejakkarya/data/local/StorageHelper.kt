package com.example.jejakkarya.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StorageHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jejak_karya_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- Bookmark Storage ---
    private val BOOKMARKS_KEY = "bookmarks_key"
    private val _bookmarksFlow = MutableStateFlow<List<ArtworkEntity>>(getSavedBookmarks())
    val bookmarksFlow: StateFlow<List<ArtworkEntity>> = _bookmarksFlow

    private fun getSavedBookmarks(): List<ArtworkEntity> {
        val json = prefs.getString(BOOKMARKS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<ArtworkEntity>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveBookmark(artwork: ArtworkEntity) {
        val current = getSavedBookmarks().toMutableList()
        if (current.none { it.objectID == artwork.objectID }) {
            current.add(0, artwork) // Tambah di posisi teratas
            prefs.edit().putString(BOOKMARKS_KEY, gson.toJson(current)).apply()
            _bookmarksFlow.value = current
        }
    }

    fun removeBookmark(id: Int) {
        val current = getSavedBookmarks().toMutableList()
        current.removeAll { it.objectID == id }
        prefs.edit().putString(BOOKMARKS_KEY, gson.toJson(current)).apply()
        _bookmarksFlow.value = current
    }

    fun removeBookmarks(ids: List<Int>) {
        val current = getSavedBookmarks().toMutableList()
        current.removeAll { ids.contains(it.objectID) }
        prefs.edit().putString(BOOKMARKS_KEY, gson.toJson(current)).apply()
        _bookmarksFlow.value = current
    }

    fun isBookmarked(id: Int): Boolean {
        return getSavedBookmarks().any { it.objectID == id }
    }

    // --- Home Screen Cache Storage ---
    fun saveHomeCache(query: String, artworks: List<ArtworkEntity>) {
        val key = "home_cache_$query"
        prefs.edit().putString(key, gson.toJson(artworks)).apply()
    }

    fun getHomeCache(query: String): List<ArtworkEntity> {
        val key = "home_cache_$query"
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<ArtworkEntity>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearAllCache() {
        val keys = prefs.all.keys
        val editor = prefs.edit()
        keys.forEach { key ->
            if (key.startsWith("home_cache_")) {
                editor.remove(key)
            }
        }
        editor.apply()
    }
}
