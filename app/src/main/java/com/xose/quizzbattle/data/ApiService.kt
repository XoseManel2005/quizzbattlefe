package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.LoginRequest
import com.xose.quizzbattle.model.LoginResponse
import com.xose.quizzbattle.model.RegisterRequest
import com.xose.quizzbattle.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/authenticate")
    fun login(@Body request: LoginRequest): Call<User>
    @POST("/users/save")
    fun register(@Body request: RegisterRequest): Call<User>
}