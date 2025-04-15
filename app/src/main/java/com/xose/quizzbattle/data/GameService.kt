package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.Game
import retrofit2.http.GET

interface GameService {
    @GET("games")
    suspend fun getGames(): List<Game>
}
