package com.example.searchbook


import BookDoc
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.TextButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.searchbook.ui.theme.SearchBookTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchBookTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
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
            // Показываем только если текущий экран в списке bottomNavScreens
            if (currentRoute != null && bottomNavScreens.any { routePattern ->
                    // Обрабатываем параметризованные маршруты, чтобы сравнить только префикс
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
                SearchScreen(navController)
            }
            composable("booksList/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                BooksListScreen(category, booksViewModel, navController)
            }
            composable("my_books") {
                MyBooksScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("details/{workId}") { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: ""
                BookDetailsScreen(workId = workId, navController = navController)
            }
        }
    }
}







@Composable
fun GreetingScreen(navController: NavController) {
    val buttonModifier = Modifier
        .width(200.dp)
        .height(50.dp)

    val loginColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF16b88c),
        contentColor = Color(0xfff5e5e9)
    )

    val registerColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xfff5e5e9),
        contentColor = Color(0xff000000)
    )

    val lobsterFont = FontFamily(Font(R.font.lobster_regular))


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фон
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Поиск рецептов",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = lobsterFont
                ),
                modifier = Modifier
                    .padding(bottom = 100.dp)
                    .offset(y = (-50).dp)
            )

            AnimatedButton(
                text = "Войти",
                colors = loginColors,
                modifier = buttonModifier,
                onClick = { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedButton(
                text = "Регистрация",
                colors = registerColors,
                modifier = buttonModifier,
                onClick = { navController.navigate("register") }
            )
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
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {

    // Состояние для текстовых полей
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    val buttonModifier = Modifier
        .width(200.dp)
        .height(40.dp)

    val loginColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF16b88c),
        contentColor = Color(0xfff5e5e9)
    )

    val loginResult by authViewModel.loginResult.observeAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginResult) {
        Log.d("LoginDebug", "LaunchedEffect triggered with result: $loginResult")

        loginResult?.let { result ->
            isLoading = false
            result.fold(
                onSuccess = { responseText ->
                    Log.d("LoginDebug", "Login success response: $responseText")

                    if (responseText.contains("success", ignoreCase = true)) {
                        navController.navigate("search") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        errorMessage = "Неизвестный ответ от сервера: $responseText"
                    }
                },
                onFailure = {
                    Log.d("LoginDebug", "Login failed: ${it.message}") // <-- и это
                    errorMessage = it.message ?: "Ошибка входа"
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фон
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = { navController.navigate("greeting") },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.DarkGray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp), // отступ от верха
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вход",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username or Email") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember + Forgot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                    Text("Запомнить меня", fontSize = 14.sp)
                }

                Text(
                    text = "Забыли пароль?",
                    color = Color(0xFF1A8F84),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // обработка клика на забытый пароль
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Заполните все поля"
                    } else if (!isLoading) {
                        isLoading = true
                        authViewModel.loginUser(username, password)
                    }
                },
                modifier = buttonModifier
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = loginColors,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Войти", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}


@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    val buttonModifier = Modifier
        .width(200.dp)
        .height(40.dp)

    val loginColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF16b88c),
        contentColor = Color(0xfff5e5e9)
    )

    // Поля ввода
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    // Для показа сообщений
    val context = LocalContext.current

    // Наблюдаем за результатом регистрации
    val registerResult by authViewModel.registerResult.observeAsState()

    // Проверка результата и навигация при успехе
    LaunchedEffect(registerResult) {
        registerResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
            } else {
                Toast.makeText(context, "Ошибка: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фон
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Назад
        IconButton(
            onClick = { navController.navigate("greeting") },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.DarkGray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Создание аккаунта",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = fullName.value,
                onValueChange = { fullName.value = it },
                label = { Text("Fullname") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Простая проверка
                    if (password.value != confirmPassword.value) {
                        Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    } else if (email.value.isBlank() || fullName.value.isBlank() || password.value.isBlank()) {
                        Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.registerUser(
                            username = fullName.value,
                            email = email.value,
                            password = password.value
                        )
                    }
                },
                modifier = buttonModifier
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = loginColors
            ) {
                Text("Зарегистрироваться", color = Color.White)
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text("Уже есть аккаунт?", fontSize = 14.sp)
            Text(
                text = "Войти.",
                color = Color(0xFF1A8F84),
                fontSize = 14.sp,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}


@Composable
fun SearchScreen(navController: NavController) {
    val categories = listOf(
        "Fiction", "Science", "History", "Art", "Fantasy", "Technology", "Education"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Выберите категорию", style = MaterialTheme.typography.headlineSmall)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category) {
                    navController.navigate("booksList/$category") // ✅ исправленный маршрут
                }
            }
        }
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
fun BooksListScreen(category: String, viewModel: BooksViewModel, navController: NavController) {
    val books = viewModel.books
    val isLoading = viewModel.isLoading

    LaunchedEffect(category) {
        viewModel.searchBooks(category)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Категория: $category", style = MaterialTheme.typography.headlineSmall)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(books) { book ->
                    BookCard(book, navController)
                }
            }
        }
    }
}



@Composable
fun BookCard(book: BookDoc, navController: NavController) {
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
            // Используем переведённое название, если есть
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


@Composable
fun MyBooksScreen(navController: NavController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Мои книги")
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Профиль")
    }
}


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


@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}








