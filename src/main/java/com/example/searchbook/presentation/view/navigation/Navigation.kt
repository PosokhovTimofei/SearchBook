package com.example.searchbook.presentation.view.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.searchbook.AuthViewModel
import com.example.searchbook.BooksViewModel
import com.example.searchbook.ThemeViewModel
import com.example.searchbook.presentation.view.bars.BottomNavigationBar
import com.example.searchbook.presentation.view.details.BookDetailsScreen
import com.example.searchbook.presentation.view.favorite.MyBooksScreen
import com.example.searchbook.presentation.view.greeting.GreetingScreen
import com.example.searchbook.presentation.view.list.BooksListScreen
import com.example.searchbook.presentation.view.login.LoginScreen
import com.example.searchbook.presentation.view.profile.ProfileScreen
import com.example.searchbook.presentation.view.register.RegisterScreen
import com.example.searchbook.presentation.view.search.SearchScreen
import kotlin.text.startsWith

@Composable
fun Navigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val booksViewModel: BooksViewModel = viewModel()

    // Список экранов, на которых хотим показывать BottomNavigation
    val bottomNavScreens = listOf(
        "search",
        "booksList/{category}",
        "my_books",
        "profile",
        "details/{workId}"
    )

    // Получаем текущий маршрут
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != null && bottomNavScreens.any { routePattern ->
                    if (routePattern.contains("{")) {
                        currentRoute.startsWith(routePattern.substringBefore("{"))
                    } else {
                        currentRoute == routePattern
                    }
                }) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "greeting",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("greeting") {
                GreetingScreen(navController)
            }
            composable("login") {
                LoginScreen(navController, authViewModel)
            }
            composable("register") {
                RegisterScreen(navController, authViewModel)
            }
            composable("search") {
                SearchScreen(navController, booksViewModel)
            }
            composable("booksList/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                BooksListScreen(category, booksViewModel, navController)
            }
            composable("my_books") {
                MyBooksScreen(navController = navController, booksViewModel = booksViewModel)
            }
            composable("profile") {
                ProfileScreen(authViewModel, navController, themeViewModel)
            }


            composable("details/{workId}") { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: ""
                BookDetailsScreen(workId = workId, navController = navController)
            }
        }
    }
}