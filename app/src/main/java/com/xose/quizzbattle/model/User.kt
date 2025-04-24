package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
open class User(
    val id: Long? = null,
    var username: String,
    var email: String? = null,
    var password: String,
    var role: Role = Role.PLAYER,
    var googleId: String? = null,
    var fcmToken: String? = null,
    var profilePicture: String? = null
) : JavaSerializable {

    @Serializable
    enum class Role : JavaSerializable {
        ADMIN,
        PLAYER
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', email=$email, role=$role, googleId=$googleId, fcmToken=$fcmToken, profilePicture=$profilePicture)"
    }

    open fun getInfo(): String {
        return "User: $email"
    }
}
