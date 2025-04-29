package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.Category
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.LoginRequest
import com.xose.quizzbattle.model.LoginResponse
import com.xose.quizzbattle.model.Question
import com.xose.quizzbattle.model.RegisterRequest
import com.xose.quizzbattle.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("/authenticate")
    fun login(@Body request: LoginRequest): Call<User>
    @POST("/users/save")
    fun register(@Body request: RegisterRequest): Call<User>
    @GET("category/find/all")
    fun getCategories(@Header("Authorization") token: String): Call<List<Category>>

    @GET("questions/find/random/by/category")
    fun getRandomQuestionsByCategory(@Query("categoryName") categoryName: String): Call<Question>


}