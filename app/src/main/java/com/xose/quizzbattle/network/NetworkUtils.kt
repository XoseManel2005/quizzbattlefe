package com.xose.quizzbattle.network

import android.content.Context
import android.util.Log
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.utils.SharedPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NetworkUtils {

    // Este método se usa para autenticar al usuario
    fun authenticateUser(context: Context, user: User) {
        val call: Call<UserAuthResponse> = ApiClient.apiService.authenticateUser(user)

        call.enqueue(object : Callback<UserAuthResponse> {
            override fun onResponse(call: Call<UserAuthResponse>, response: Response<UserAuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    authResponse?.let {
                        Log.d("NetworkUtils", "Token recibido: ${it.token}")
                        // Guardamos el token en SharedPreferences
                        SharedPrefs.saveAuthToken(context, it.token)
                    }
                } else {
                    Log.e("NetworkUtils", "Error en la autenticación: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserAuthResponse>, t: Throwable) {
                Log.e("NetworkUtils", "Error de red: ${t.message}")
            }
        })
    }
}
