package com.example.searchbook

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    private val _books = mutableStateListOf<Book>()
    val books: List<Book> = _books

    var isLoading by mutableStateOf(false)
        private set

    fun searchBooks(category: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = GoogleBooksClient.api.searchBooks("subject:$category")
                _books.clear()
                response.items?.let {
                    _books.addAll(it)
                }
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error fetching books", e)
            } finally {
                isLoading = false
            }
        }
    }
}
