package com.example.searchbook

import BookDetails
import BookDoc
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    private val _books = mutableStateListOf<BookDoc>()
    val books: List<BookDoc> = _books

    var isLoading by mutableStateOf(false)
        private set

    fun searchBooks(category: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = OpenLibraryClient.api.searchBooks(
                    query = category
                )
                _books.clear()
                response.docs?.let { docs ->
                    val filtered = docs.filter { it.language?.contains("rus") == true }
                    _books.addAll(filtered)
                }
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error fetching books", e)
            } finally {
                isLoading = false
            }
        }
    }
}

class BookDetailsViewModel : ViewModel() {
    var bookDetails by mutableStateOf<BookDetails?>(null)
        private set

    var translatedDescription by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadBookDetails(workId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = OpenLibraryClient.api.getBookDetails(workId)
                bookDetails = result
                val descriptionText = when (val desc = result.description) {
                    is String -> desc
                    is Map<*, *> -> desc["value"] as? String ?: ""
                    else -> ""
                }
                if (descriptionText.isNotBlank()) {
                    translateDescription(descriptionText)
                } else {
                    translatedDescription = "Описание недоступно"
                }
            } catch (e: Exception) {
                Log.e("BookDetailsVM", "Error loading book details", e)
                translatedDescription = "Ошибка загрузки описания"
            } finally {
                isLoading = false
            }
        }
    }

    private fun translateDescription(text: String) {
        val options = com.google.mlkit.nl.translate.TranslatorOptions.Builder()
            .setSourceLanguage(com.google.mlkit.nl.translate.TranslateLanguage.ENGLISH)
            .setTargetLanguage(com.google.mlkit.nl.translate.TranslateLanguage.RUSSIAN)
            .build()

        val translator = com.google.mlkit.nl.translate.Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        translatedDescription = translatedText
                    }
                    .addOnFailureListener { e ->
                        translatedDescription = "Ошибка перевода: ${e.localizedMessage}"
                    }
            }
            .addOnFailureListener { e ->
                translatedDescription = "Ошибка загрузки модели: ${e.localizedMessage}"
            }
    }
}









