package com.example.climov_comercializadora_restjava.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    // Ajusta la IP al host donde corre el servidor si usas dispositivo físico.
    // Para emulador de Android Studio, 10.0.2.2 apunta al host.
    private const val BASE_URL = "http://10.0.2.2:8080/Ex_Comercializadora_RESTJava/api/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val api: ComercializadoraApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ComercializadoraApi::class.java)
    }
}
