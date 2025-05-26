package com.example.searchbook.presentation.view.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.searchbook.BooksViewModel.BookDetailsViewModel
import com.example.searchbook.presentation.view.components.StatItem


@Composable
fun BookDetailsScreen(
    workId: String,
    viewModel: BookDetailsViewModel = viewModel(),
    navController: NavHostController
) {
    val book = viewModel.bookDetails
    val isLoading = viewModel.isLoading
    val translatedDescription = viewModel.translatedDescription
    val translatedTitle = viewModel.translatedTitle

    LaunchedEffect(workId) {
        viewModel.loadBookDetails(workId)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        book?.let {
            val coverUrl = it.covers?.firstOrNull()?.let { id ->
                "https://covers.openlibrary.org/b/id/$id-L.jpg"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Добавлена прокрутка
            ) {
                // Обложка
                coverUrl?.let { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Название книги (переведённое, если есть)
                Text(
                    text = translatedTitle ?: it.title ?: "Без названия",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Авторы — вытаскиваем имена из authorwrapper
                val authorNames = it.authors?.mapNotNull { authorWrapper ->
                    val authorMap = authorWrapper as? Map<*, *>
                    val authorData = authorMap?.get("author") as? Map<*, *>
                    authorData?.get("name") as? String
                }

                Text(
                    text = authorNames?.joinToString(", ") ?: "Автор неизвестен",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Переключатели "Текст"/"Аудио"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { /* Текст */ }) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Текст", color = Color.White)
                    }
                    TextButton(onClick = { /* Аудио */ }) {
                        Icon(Icons.Default.Headset, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Аудио", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Описание книги (переведённое)
                Text(
                    text = translatedDescription ?: "Описание загружается...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Статистика
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("181", "Страница")
                    StatItem("57.2K", "Читают")
                    StatItem("58.9K", "Цитат")
                    StatItem("2.5K", "Впечатления")
                    StatItem("154", "Полки")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопки Читать / Слушать
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(Color.DarkGray),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* Читать */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Читать", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Button(
                        onClick = { /* Слушать */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Слушать", color = Color.White)
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Информация о книге не найдена", color = Color.White)
        }
    }
}