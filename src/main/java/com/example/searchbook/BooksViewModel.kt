package com.example.searchbook


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class BooksViewModel : ViewModel() {
    private val _books = mutableStateListOf<Book>()
    val books: List<Book> = _books

    var isLoading by mutableStateOf(false)
        private set

    fun searchBooks(category: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val url = "https://www.googleapis.com/books/v1/volumes" +
                        "?q=subject:$category" +
                        "&maxResults=40" +
                        "&langRestrict=ru"

                val result = URL(url).readText()
                val json = JSONObject(result)
                val items = json.optJSONArray("items") ?: JSONArray()

                _books.clear()

                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val volumeInfo = item.getJSONObject("volumeInfo")

                    val title = volumeInfo.optString("title", "Без названия")
                    val authors = volumeInfo.optJSONArray("authors")?.let { array ->
                        List(array.length()) { index -> array.getString(index) }
                    }

                    val imageLinks = volumeInfo.optJSONObject("imageLinks")
                    val thumbnail = imageLinks?.optString("thumbnail")
                    _books.add(
                        Book(
                            id = item.optString("id"),
                            volumeInfo = VolumeInfo(
                                title = title,
                                authors = authors,
                                imageLinks = ImageLinks(thumbnail = thumbnail ?: "")
                            )
                        )
                    )

                }


            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }
}
