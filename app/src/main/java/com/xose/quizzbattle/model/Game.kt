package com.xose.quizzbattle.model

import kotlinx.serialization.Serializable

data class Game(
    val id: Long,
    val player1: User,
    val player2: User,
    var status: Status,
    var turn: User,
    var scorePlayer1: Int = 0,
    var scorePlayer2: Int = 0,
    var starsPlayer1: Int = 0,
    var starsPlayer2: Int = 0,
    var winner: User
) {

    @Serializable
    enum class Status {
        ONGOING,
        FINISHED
    }

}
