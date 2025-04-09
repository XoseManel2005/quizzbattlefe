package com.xose.quizzbattle.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    // Guardar el token de autenticación
    fun saveAuthToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    // Recuperar el token de autenticación
    fun getAuthToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    // Eliminar el token (cuando el usuario se desloguea)
    fun clearAuthToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.apply()
    }
}
