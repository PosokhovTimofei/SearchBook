package com.example.searchbook.data.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.searchbook.OpenLibraryClient
import com.example.searchbook.data.model.BookDoc
import com.example.searchbook.data.repository.FavoritesRepository
import com.example.searchbook.utils.TranslatorHelper
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {

    private val repository = FavoritesRepository()
    private val _books = mutableStateListOf<BookDoc>()
    val books: List<BookDoc> get() = _books

    private val _favoriteBooks = mutableStateListOf<BookDoc>()
    val favoriteBooks: List<BookDoc> get() = _favoriteBooks

    var isLoading by mutableStateOf(false)
        private set

    fun searchBooks(category: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = OpenLibraryClient.api.searchBooks(category)
                val favorites = repository.getFavorites(userId = 1)
                _books.clear()

                response.docs?.filter { it.language?.contains("rus") == true }?.forEach { book ->
                    val isFavorite = favorites.any { it.key == book.key }
                    val translatedTitle = book.title?.let { TranslatorHelper.translateTextCached(it) }

                    _books.add(book.copy(isFavorite = isFavorite, translatedTitle = translatedTitle))
                }
            } catch (e: Exception) {
                Log.e("BooksVM", "Ошибка поиска книг", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleFavorite(book: BookDoc) {
        viewModelScope.launch {
            val isFavorite = favoriteBooks.any { it.key == book.key }
            val updatedBook = book.copy(isFavorite = !isFavorite)
            val success = if (isFavorite) {
                repository.removeFromFavorites(book, 1)
            } else {
                repository.addToFavorites(book, 1)
            }

            if (success) {
                updateFavoritesState(updatedBook)
            }
        }
    }

    private fun updateFavoritesState(updatedBook: BookDoc) {
        val index = _books.indexOfFirst { it.key == updatedBook.key }
        if (index != -1) _books[index] = updatedBook

        _favoriteBooks.removeAll { it.key == updatedBook.key }
        if (updatedBook.isFavorite) _favoriteBooks.add(updatedBook)
    }

    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val favorites = repository.getFavorites(userId).map { it.copy(isFavorite = true) }
                _favoriteBooks.clear()
                _favoriteBooks.addAll(favorites)

                val updated = _books.map {
                    if (favorites.any { fav -> fav.key == it.key }) it.copy(isFavorite = true) else it
                }
                _books.clear()
                _books.addAll(updated)
            } catch (e: Exception) {
                Log.e("BooksVM", "Ошибка загрузки избранного", e)
            } finally {
                isLoading = false
            }
        }
    }
}