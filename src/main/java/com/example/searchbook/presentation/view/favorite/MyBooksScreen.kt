package com.example.searchbook.presentation.view.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.searchbook.data.viewmodel.BooksViewModel

import com.example.searchbook.presentation.view.search.BookCard

@Composable
fun MyBooksScreen(
    booksViewModel: BooksViewModel = viewModel(),
    navController: NavController
) {
    // Используем derivedStateOf с by, чтобы Compose отслеживал изменения favoriteBooks
    val favoriteBooks by remember { derivedStateOf { booksViewModel.favoriteBooks } }
    val isLoading by remember { derivedStateOf { booksViewModel.isLoading } }

    LaunchedEffect(Unit) {
        booksViewModel.loadFavorites(userId = 1)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (favoriteBooks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Избранных книг нет", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Избранное",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(
                items = favoriteBooks,
                key = { it.key ?: it.title ?: it.hashCode().toString() }
            ) { book ->
                BookCard(
                    book = book,
                    navController = navController,
                    onFavoriteClick = { clickedBook ->
                        booksViewModel.toggleFavorite(clickedBook)
                    }
                )
            }
        }
    }
}