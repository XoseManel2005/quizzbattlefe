package com.xose.quizzbattle.utils

import android.content.Context

object AuthUtils {

    // Verificar si el usuario está autenticado (es decir, si hay un token disponible)
    fun isAuthenticated(context: Context): Boolean {
        val token = SharedPrefs.getAuthToken(context)
        return token != null
    }
}
