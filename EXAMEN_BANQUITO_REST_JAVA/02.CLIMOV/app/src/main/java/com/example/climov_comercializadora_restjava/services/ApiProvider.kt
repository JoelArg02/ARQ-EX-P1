package com.example.climov_comercializadora_restjava.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    // Para desarrollo local con Docker backend en puerto 8081
    // - Emulador Android Studio: 10.0.2.2 apunta al localhost del host
    // - Dispositivo físico en la misma red: usar IP del host (ej: 192.168.x.x)
    // LOCAL EMULADOR: private const val BASE_URL = "http://10.0.2.2:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/"
    // LOCAL FISICO: private const val BASE_URL = "http://192.168.137.1:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/"
    // PRODUCCION (DigitalOcean):
    private const val BASE_URL = "http://159.203.120.118:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/"


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
