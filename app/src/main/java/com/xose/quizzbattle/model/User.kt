package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import kotlinx.datetime.LocalDateTime as KLocalDateTime

@Serializable
open class User(
    val id: Long? = null,                        // Valor por defecto para 'id'
    var username: String,
    var email: String? = null,
    var password: String,
    var role: Role = Role.PLAYER,            // Valor por defecto para 'role'
    var googleId: String? = null,
    var fcmToken: String? = null,
    var profilePicture: String? = null
)
{

    @Serializable
    enum class Role {
        ADMIN,
        PLAYER
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', email=$email, role=$role, googleId=$googleId, fcmToken=$fcmToken, profilePicture=$profilePicture)"
    }

    // Método para obtener información básica del usuario
    open fun getInfo(): String {
        return "User: $email"
    }

}
