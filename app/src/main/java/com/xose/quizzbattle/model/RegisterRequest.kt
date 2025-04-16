package com.xose.quizzbattle.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String = "PLAYER"
)