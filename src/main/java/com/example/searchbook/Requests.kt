package com.example.searchbook

// UserRegisterRequest.kt
data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

// UserLoginRequest.kt
data class UserLoginRequest(
    val username: String,
    val password: String
)

// ApiResponse.kt
data class ApiResponse(
    val success: Boolean,
    val message: String
)