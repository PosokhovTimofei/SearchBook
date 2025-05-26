package com.example.searchbook


import BookDoc
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.TextButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.rememberAsyncImagePainter
import com.example.searcbook.R
import com.example.searchbook.BooksViewModel.BookDetailsViewModel
import com.example.searchbook.presentation.view.details.BookDetailsScreen
import com.example.searchbook.presentation.view.greeting.GreetingScreen
import com.example.searchbook.presentation.view.login.LoginScreen
import com.example.searchbook.presentation.view.register.RegisterScreen
import com.example.searchbook.presentation.view.search.BookCard
import com.example.searchbook.presentation.view.search.SearchScreen
import com.example.searchbook.ui.theme.SearchBookTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme

            SearchBookTheme(darkTheme = isDarkTheme) {
                // Передаём themeViewModel в Navigation (или куда нужно)
                Navigation(themeViewModel)
            }
        }
    }
}

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

@Composable
fun AnimatedButton(
    text: String,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "ButtonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(8.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = colors,
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(text)
    }
}

@Composable
internal fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun EmptyResults() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(category, style = MaterialTheme.typography.titleMedium)
        }
    }
}


@Composable
fun BooksListScreen(
    category: String,
    viewModel: BooksViewModel,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val books = viewModel.books
    val isLoading = viewModel.isLoading

    // Загружаем книги по категории или поисковому запросу
    LaunchedEffect(category, searchQuery) {
        if (searchQuery.isBlank()) {
            viewModel.searchBooks(category)
        } else {
            viewModel.searchBooks(searchQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Сначала категория
        Text("Категория: $category", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Потом строка поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск книг...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(books) { book ->
                    BookCard(book, navController) { selectedBook ->
                        viewModel.toggleFavorite(selectedBook)
                    }
                }
            }

        }
    }
}

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

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val username by authViewModel.currentUsername.observeAsState("Пользователь")
    val isDarkTheme by themeViewModel.isDarkTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Профиль", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Имя пользователя:", fontWeight = FontWeight.Bold)
        Text(text = username.toString(), fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Тёмная тема", modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { themeViewModel.toggleTheme() }
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Мои книги", "my_books", Icons.Default.Book),
        BottomNavItem("Поиск", "search", Icons.Default.Search),
        BottomNavItem("Профиль", "profile", Icons.Default.Person)
    )

    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination == item.route,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("search") { inclusive = false } // базовый маршрут
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}






