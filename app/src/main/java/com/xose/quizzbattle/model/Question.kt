package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Long,
    val correctAnswer: String,
    val imageUrl: String?,
    val questionText: String,
    val wrongOption1: String,
    val wrongOption2: String,
    val wrongOption3: String
)
