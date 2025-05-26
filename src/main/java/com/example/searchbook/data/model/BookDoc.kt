package com.example.searchbook.data.model

data class BookDoc(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val language: List<String>?,
    val cover_i: Int?,
    val key: String?,
    val isbn: List<String>? = null,
    var translatedTitle: String? = null,
    var isFavorite: Boolean = false
)