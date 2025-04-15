package com.xose.quizzbattle.util // o el paquete donde lo tengas

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.xose.quizzbattle.model.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quizzPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val LOGGED_USER = "LOGGED_USER"
    }

    private val gson = Gson()

    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun saveLoggedUser(user: User?) {
        val json = gson.toJson(user)
        prefs.edit().putString(LOGGED_USER, json).apply()
    }

    fun getLoggedUser(): User? {
        val json = prefs.getString(LOGGED_USER, null)
        return if (json != null) gson.fromJson(json, User::class.java) else null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
