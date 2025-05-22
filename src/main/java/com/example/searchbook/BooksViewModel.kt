package com.example.searchbook

import BookDetails
import BookDoc
import FavoriteBookRequest
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.text.set

class BooksViewModel : ViewModel() {
    private val _books = mutableStateListOf<BookDoc>()
    val books: List<BookDoc> = _books

    private val repository = FavoritesRepository()

    var isLoading by mutableStateOf(false)
        private set

    // Кэш для переводов: ключ - оригинальный текст, значение - перевод
    private val translationCache = mutableMapOf<String, String>()

    private val _favoriteBooks = mutableStateListOf<BookDoc>()
    val favoriteBooks: List<BookDoc> get() = _favoriteBooks


    // Обновленная функция перевода с кэшем
    suspend fun translateTextCached(text: String): String {
        if (translationCache.containsKey(text)) {
            return translationCache[text] ?: text
        }

        val translated = translateTextSuspend(text)
        translationCache[text] = translated
        return translated
    }

    fun searchBooks(category: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = OpenLibraryClient.api.searchBooks(query = category)
                val favoriteBooks = repository.getFavorites(userId = 1) // Загружаем избранное

                _books.clear()
                response.docs?.filter { it.language?.contains("rus") == true }?.forEach { book ->
                    // Проверяем, есть ли книга в избранном
                    val isFavorite = favoriteBooks.any { favorite -> favorite.key == book.key }
                    _books.add(book.copy(isFavorite = isFavorite))
                }
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Ошибка загрузки книг", e)
            } finally {
                isLoading = false
            }
        }
    }


    class BookDetailsViewModel : ViewModel() {
        var bookDetails by mutableStateOf<BookDetails?>(null)
            private set

        var translatedDescription by mutableStateOf<String?>(null)
            private set

        var translatedTitle by mutableStateOf<String?>(null)
            private set

        var isLoading by mutableStateOf(false)
            private set

        // Можно тоже добавить простой кэш для переводов здесь, если нужно
        private val translationCache = mutableMapOf<String, String>()

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
                        translateText(descriptionText) { translated ->
                            translatedDescription = translated
                        }
                    } else {
                        translatedDescription = "Описание недоступно"
                    }

                    result.title?.let { title ->
                        translateText(title) { translated ->
                            translatedTitle = translated
                        }
                    }

                } catch (e: Exception) {
                    Log.e("BookDetailsVM", "Ошибка загрузки деталей книги", e)
                    translatedDescription = "Ошибка загрузки описания"
                    translatedTitle = "Ошибка загрузки названия"
                } finally {
                    isLoading = false
                }
            }
        }

        private fun translateText(text: String, onResult: (String) -> Unit) {
            // Проверка кэша перед переводом
            if (translationCache.containsKey(text)) {
                onResult(translationCache[text] ?: text)
                return
            }

            val options = com.google.mlkit.nl.translate.TranslatorOptions.Builder()
                .setSourceLanguage(com.google.mlkit.nl.translate.TranslateLanguage.ENGLISH)
                .setTargetLanguage(com.google.mlkit.nl.translate.TranslateLanguage.RUSSIAN)
                .build()

            val translator = com.google.mlkit.nl.translate.Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            translationCache[text] = translatedText
                            onResult(translatedText)
                        }
                        .addOnFailureListener { e ->
                            onResult("Ошибка перевода: ${e.localizedMessage}")
                        }
                }
                .addOnFailureListener { e ->
                    onResult("Ошибка загрузки модели: ${e.localizedMessage}")
                }
        }
    }

    // suspend версия перевода без кэша (внешне используем translateTextCached)
    suspend fun translateTextSuspend(text: String): String {
        return suspendCancellableCoroutine { cont ->
            val options = com.google.mlkit.nl.translate.TranslatorOptions.Builder()
                .setSourceLanguage(com.google.mlkit.nl.translate.TranslateLanguage.ENGLISH)
                .setTargetLanguage(com.google.mlkit.nl.translate.TranslateLanguage.RUSSIAN)
                .build()

            val translator = com.google.mlkit.nl.translate.Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translated ->
                            cont.resume(translated)
                        }
                        .addOnFailureListener { e ->
                            cont.resume("Ошибка перевода: ${e.localizedMessage}")
                        }
                }
                .addOnFailureListener { e ->
                    cont.resume("Ошибка загрузки модели: ${e.localizedMessage}")
                }
        }
    }

    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val booksFromDb = repository.getFavorites(userId)
                val markedFavorites = booksFromDb.map { it.copy(isFavorite = true) }

                _favoriteBooks.clear()
                _favoriteBooks.addAll(markedFavorites)


                // Обновляем _books — если книга есть в favorites, ставим isFavorite = true
                val updatedBooks = _books.map { book ->
                    if (booksFromDb.any { it.key == book.key }) {
                        book.copy(isFavorite = true)
                    } else {
                        book
                    }
                }
                _books.clear()
                _books.addAll(updatedBooks)

            } catch (e: Exception) {
                Log.e("MyBooksViewModel", "Ошибка загрузки избранных книг", e)
            } finally {
                isLoading = false
            }
        }
    }


    fun toggleFavorite(book: BookDoc) {
        viewModelScope.launch {
            try {
                // Проверяем текущее состояние книги в списке _favoriteBooks
                val isCurrentlyFavorite = _favoriteBooks.any { it.key == book.key }
                val shouldAddToFavorites = !isCurrentlyFavorite

                val updatedBook = book.copy(isFavorite = shouldAddToFavorites)

                val success = if (shouldAddToFavorites) {
                    repository.addToFavorites(updatedBook, 1)
                } else {
                    repository.removeFromFavorites(updatedBook, 1)
                }

                if (success) {
                    // Обновляем _books
                    val index = _books.indexOfFirst { it.key == book.key }
                    if (index != -1) {
                        _books[index] = updatedBook
                    }

                    // Обновляем _favoriteBooks
                    if (shouldAddToFavorites) {
                        _favoriteBooks.add(updatedBook)
                    } else {
                        _favoriteBooks.removeAll { it.key == book.key }
                    }

                    // Принудительно обновляем список, чтобы триггерить Compose
                    _favoriteBooks.toList().also {
                        _favoriteBooks.clear()
                        _favoriteBooks.addAll(it)
                    }

                } else {
                    Log.e("BooksViewModel", "Ошибка обновления избранного на сервере")
                }
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Ошибка при обновлении избранного", e)
            }
        }
    }




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

}


    fun BookDoc.toRequest(userId: Int): FavoriteBookRequest {
        return FavoriteBookRequest(
            key = this.key ?: "",
            title = this.title,
            author = this.author_name?.joinToString(", "),
            cover_i = this.cover_i,
            userId = userId
        )
    }

    object BackendClient {
        val api: ApiService by lazy {
            ApiService.create()
        }
    }



