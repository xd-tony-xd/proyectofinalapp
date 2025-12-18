package com.example.sistema2.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Si usas emulador de Android Studio
    private const val BASE_URL = "https://electoral-laurice-tonyxyz-524abfe8.koyeb.app/api/"

// Si usas un dispositivo físico, reemplaza con la IP de tu PC en la red local
// private const val BASE_URL = "http://192.168.X.X:8000/api/"


    // Logging de requests/responses
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API listo para usar
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
