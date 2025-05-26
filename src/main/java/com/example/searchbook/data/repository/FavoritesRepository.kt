package com.example.searchbook.data.repository

import android.util.Log
import com.example.searchbook.data.model.BackendClient
import com.example.searchbook.data.model.BookDoc
import com.example.searchbook.data.model.toRequest


class FavoritesRepository {
    private val api = BackendClient.api

    suspend fun getFavorites(userId: Int): List<BookDoc> {
        return try {
            api.getFavoriteBooks(userId).also {
                Log.d("FavoritesRepo", "Got favorites: ${it.size} books")
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepo", "Error getting favorites", e)
            emptyList()
        }
    }

    suspend fun addToFavorites(book: BookDoc, userId: Int): Boolean {
        return try {
            val response = api.addToFavorites(book.toRequest(userId))
            response.isSuccessful.also {
                if (it) Log.d("FavoritesRepo", "Added to favorites: ${book.key}")
                else Log.e("FavoritesRepo", "Failed to add: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepo", "Error adding to favorites", e)
            false
        }
    }

    suspend fun removeFromFavorites(book: BookDoc, userId: Int): Boolean {
        return try {
            val response = api.removeFromFavorites(book.toRequest(userId))
            response.isSuccessful.also {
                if (it) Log.d("FavoritesRepo", "Removed from favorites: ${book.key}")
                else Log.e("FavoritesRepo", "Failed to remove: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepo", "Error removing from favorites", e)
            false
        }
    }
}
