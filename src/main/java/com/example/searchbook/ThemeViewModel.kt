package com.example.searchbook

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    // Храним текущее состояние темы (false — светлая, true — тёмная)
    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    // Метод переключения темы
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
