package com.example.searchbook

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val username: String,
    val password: String
)
