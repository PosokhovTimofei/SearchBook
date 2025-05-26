package com.example.searchbook.presentation.view.search

import BookDoc
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun BookCard(
    book: BookDoc,
    navController: NavController,
    onFavoriteClick: (BookDoc) -> Unit
) {
    val coverUrl = book.cover_i?.let {
        "https://covers.openlibrary.org/b/id/$it-L.jpg"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val workId = book.key?.removePrefix("/works/") ?: return@clickable
                navController.navigate("details/$workId")
            }
            .padding(8.dp)
    ) {
        if (coverUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(coverUrl),
                contentDescription = "Обложка книги",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.translatedTitle ?: book.title ?: "Нет названия",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = book.author_name?.joinToString(", ") ?: "Автор неизвестен",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            book.first_publish_year?.let {
                Text(
                    text = "Год: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        IconButton(
            onClick = {
                onFavoriteClick(book)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "В избранное",
                tint = if (book.isFavorite) Color.Red else Color.Gray
            )
        }
    }
}