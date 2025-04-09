package com.xose.quizzbattle.network


import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.Call
import retrofit2.http.POST

object ApiClient {

    private const val BASE_URL = "https://localhost:8443/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)// Usamos KotlinxSerializationConverterFactory correctamente
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
