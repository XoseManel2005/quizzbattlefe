package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import kotlinx.datetime.LocalDateTime as KLocalDateTime

@Serializable
open class User(
    val id: Long = 0,                        // Valor por defecto para 'id'
    var username: String? = null,
    var email: String,
    var password: String,
    var role: Role = Role.PLAYER,            // Valor por defecto para 'role'
    var googleId: String? = null,
    var fcmToken: String? = null,
    var profilePicture: String? = null,
    @Contextual var createdAt: KLocalDateTime? = null,
    @Contextual var updatedAt: KLocalDateTime? = null,
    @Contextual var lastLogin: KLocalDateTime? = null
)
{

    @Serializable
    enum class Role {
        ADMIN,
        PLAYER
    }

    // Método para obtener información básica del usuario
    open fun getInfo(): String {
        return "User: $email"
    }

}
