package com.example.searchbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val apiService = ApiService.create()

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    // Добавлено: для хранения имени пользователя
    private val _currentUsername = MutableLiveData<String?>()
    val currentUsername: LiveData<String?> = _currentUsername

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.registerUser(UserRegisterRequest(username, email, password))
                if (response.isSuccessful) {
                    _registerResult.value = Result.success(response.body() ?: "Empty response")
                } else {
                    _registerResult.value = Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.loginUser(UserLoginRequest(username, password))
                if (response.isSuccessful) {
                    _loginResult.value = Result.success(response.body()!!)
                    _currentUsername.value = username // Сохраняем имя пользователя
                } else {
                    _loginResult.value = Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}
