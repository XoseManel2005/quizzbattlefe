package com.xose.quizzbattle.network

import com.xose.quizzbattle.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("authenticate")
    fun authenticateUser(@Body user: User): Call<UserAuthResponse>
}
