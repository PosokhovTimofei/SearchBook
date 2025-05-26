package com.example.searchbook.presentation.view.greeting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.searcbook.R
import com.example.searchbook.AnimatedButton

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
                "Поиск книг",
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