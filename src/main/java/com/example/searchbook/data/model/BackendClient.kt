package com.example.searchbook.data.model

import com.example.searchbook.ApiService

object BackendClient {
    val api: ApiService by lazy {
        ApiService.create()
    }
}