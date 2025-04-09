package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.LoginRequest
import com.xose.quizzbattle.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/authenticate")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}