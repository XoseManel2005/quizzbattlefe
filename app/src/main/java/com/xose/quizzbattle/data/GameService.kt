package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.Game
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface GameService {
    @GET("games/find/all")
    suspend fun getGames(@Query("username") username: String, @Query("status") status: String): List<Game>

    @PUT("games/update")
    fun updateGame(@Body request: Game): Call<Game>

    @POST("games/create")
    fun createRandomGame(@Query("player1") username: String): Call<Game>
}