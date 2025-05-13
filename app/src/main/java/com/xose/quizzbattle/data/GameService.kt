package com.xose.quizzbattle.data

import com.xose.quizzbattle.model.Friendship
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.ImageRequest
import com.xose.quizzbattle.model.ImageResponse
import com.xose.quizzbattle.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GameService {
    @GET("games/find/all")
    suspend fun getGames(@Query("username") username: String, @Query("status") status: String): List<Game>

    @PUT("games/update")
    fun updateGame(@Body request: Game): Call<Game>

    @POST("games/create")
    fun createRandomGame(@Query("player1") player1: String, @Query("player2") player2: String? = null): Call<Game>

    @GET("friendship/find/all/accepted")
    suspend fun getAcceptedFriendships(@Query("username") username: String): List<Friendship>

    @GET("friendship/find/by/player/status")
    suspend fun getPendingFriendRequests(@Query("username") username: String, @Query("status") status : String): List<Friendship>

    @DELETE("friendship/delete/by/id/{id}")
    suspend fun denyFriendship(@Path("id") id: Long): Response<Void>

    @PUT("friendship/update/{id}")
    suspend fun acceptFriendship(@Path("id") id: Long): Friendship

    @POST("friendship/create")
    fun createFriendship(@Query("sender") sender: String, @Query("receiver") receiver: String? = null): Call<Friendship>

    @POST("users/upload/image-profile")
    fun uploadProfileImage(@Body request: ImageRequest): Call<String>

    @GET("/users/profile-picture/{username}")
    suspend fun getProfileImage(@Path("username") username: String): ImageResponse
    @GET("users/find/all")
    suspend fun getAllPlayers(@Query("roles") role : String): List<User>




}