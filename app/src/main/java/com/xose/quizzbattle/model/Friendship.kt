package com.xose.quizzbattle.model

import android.content.IntentSender
import kotlinx.serialization.Serializable

data class Friendship(
    val id: Long,
    val sender: User,
    val receiver: User,
    var status: Status

){

    @Serializable
    enum class Status {
        ACCEPTED,
        PENDING
    }

}

