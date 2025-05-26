package com.example.searchbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import com.example.searchbook.presentation.view.navigation.Navigation
import com.example.searchbook.ui.theme.SearchBookTheme


class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme

            SearchBookTheme(darkTheme = isDarkTheme) {
                Navigation(themeViewModel)
            }
        }
    }
}










