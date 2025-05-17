package com.example.searchbook

data class BookResponse(
    val items: List<Book>
)

data class Book(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String
)

