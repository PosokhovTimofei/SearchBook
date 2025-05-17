package com.example.searchbook

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object GoogleBooksClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val api: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }
}

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String = "AIzaSyCtcKOH_jRE_CPRFY7fOk-HgluGO1XFSxQ"
    ): BookResponse
}
