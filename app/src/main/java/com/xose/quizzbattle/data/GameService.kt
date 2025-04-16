package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.Game
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface GameService {
    @GET("games/find/all")
    suspend fun getGames(@Query("username") username: String): List<Game>
}