package com.example.searchbook.data.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.searchbook.OpenLibraryClient
import com.example.searchbook.data.model.BookDetails
import com.example.searchbook.utils.TranslatorHelper
import kotlinx.coroutines.launch

class BookDetailsViewModel : ViewModel() {
    var bookDetails by mutableStateOf<BookDetails?>(null)
        private set

    var translatedTitle by mutableStateOf<String?>(null)
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

                translatedTitle = result.title?.let { TranslatorHelper.translateTextCached(it) }

                val desc = when (val d = result.description) {
                    is String -> d
                    is Map<*, *> -> d["value"] as? String
                    else -> null
                }

                translatedDescription = desc?.let { TranslatorHelper.translateTextCached(it) }
                    ?: "Описание недоступно"
            } catch (e: Exception) {
                Log.e("BookDetailsVM", "Ошибка загрузки", e)
                translatedTitle = "Ошибка"
                translatedDescription = "Ошибка"
            } finally {
                isLoading = false
            }
        }
    }
}