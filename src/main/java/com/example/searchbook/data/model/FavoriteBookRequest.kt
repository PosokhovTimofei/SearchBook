package com.example.searchbook.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteBookRequest(
    val key: String,
    val title: String?,
    val author: String?,
    val cover_i: Int?,
    val userId: Int
)

fun BookDoc.toRequest(userId: Int): FavoriteBookRequest {
    return FavoriteBookRequest(
        key = this.key ?: "",
        title = this.translatedTitle ?: this.title ?: "",
        author = this.author_name?.joinToString(", "),
        cover_i = this.cover_i,
        userId = userId
    )
}