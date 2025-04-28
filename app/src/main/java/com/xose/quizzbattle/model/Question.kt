package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Long,
    val correctOption: String,
    val imageUrl: String?,
    val statement: String,
    val wrongOption1: String,
    val wrongOption2: String,
    val wrongOption3: String
)
