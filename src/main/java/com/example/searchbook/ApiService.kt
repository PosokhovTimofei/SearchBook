package com.example.searchbook



import com.example.searchbook.data.model.BookDoc
import com.example.searchbook.data.model.FavoriteBookRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {
    // Для текстовых ответов
    @POST("/register")
    suspend fun registerUser(@Body request: UserRegisterRequest): Response<String>

    @POST("/login")
    suspend fun loginUser(@Body request: UserLoginRequest): Response<String> // возвращаем String

    // Добавить в избранное
    @POST("/favorites/add")
    suspend fun addToFavorites(@Body request: FavoriteBookRequest): Response<String>

    // Удалить из избранного
    @POST("/favorites/remove")
    suspend fun removeFromFavorites(@Body request: FavoriteBookRequest): Response<String>

    @GET("/favorites/{userId}")
    suspend fun getFavoriteBooks(@Path("userId") userId: Int): List<BookDoc>



    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080") // .baseUrl("http://192.168.50.237:8080")
                .addConverterFactory(ScalarsConverterFactory.create()) // СНАЧАЛА текст!
                .addConverterFactory(GsonConverterFactory.create())     // ПОТОМ JSON
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()
                )
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
