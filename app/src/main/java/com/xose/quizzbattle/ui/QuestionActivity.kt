package com.xose.quizzbattle.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Category
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.Question
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : AppCompatActivity() {

    private lateinit var usuarioLogueado: User
    private lateinit var tvQuestion: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button
    private lateinit var withdraw: Button
    private var timeRemaining = 25
    private lateinit var tvSecondsRemaining: TextView
    private var countdownRunnable: Runnable? = null

    private var correctAnswer: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var game: Game? = null
    private var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        usuarioLogueado = (SessionManager(this).getLoggedUser() ?: null) as User
        tvQuestion = findViewById(R.id.tvQuestion)
        imgQuestion = findViewById(R.id.imgQuestion)
        btnAnswer1 = findViewById(R.id.btnAnswer1)
        btnAnswer2 = findViewById(R.id.btnAnswer2)
        btnAnswer3 = findViewById(R.id.btnAnswer3)
        btnAnswer4 = findViewById(R.id.btnAnswer4)
        withdraw = findViewById(R.id.btnWithdraw)
        tvSecondsRemaining = findViewById(R.id.tvSecondsRemaining)

        startCountdown()


        game = intent.getSerializableExtra("SELECTED_GAME") as? Game
        category = intent.getSerializableExtra("SELECTED_CATEGORY") as? Category

        withdraw.setOnClickListener {
            if (game?.player1?.username ?: null == game?.turn?.username ?: null) {
                game?.turn = game?.player2
            } else {
                game?.turn = game?.player1
            }
            game?.let { it1 -> updateGame(it1) }

            val intent = Intent(this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }

        game?.let { updateStarsAndPoints(it) }
        category?.let { loadRandomQuestion(it.name) }
    }

    private fun updateStarsAndPoints(gameData: Game) {
        val starsPlayer1 = listOf(
            findViewById<ImageView>(R.id.imgStar1Player1),
            findViewById<ImageView>(R.id.imgStar2Player1),
            findViewById<ImageView>(R.id.imgStar3Player1)
        )

        for (i in 0 until gameData.starsPlayer1.coerceAtMost(3)) {
            starsPlayer1[i].setImageResource(R.drawable.baseline_star_on)
        }

        val starsPlayer2 = listOf(
            findViewById<ImageView>(R.id.imgStar1Player2),
            findViewById(R.id.imgStar2Player2),
            findViewById(R.id.imgStar3Player2)
        )

        for (i in 0 until gameData.starsPlayer2.coerceAtMost(3)) {
            starsPlayer2[i].setImageResource(R.drawable.baseline_star_on)
        }

        findViewById<TextView>(R.id.tvCurrentPointsPlayer1).text = gameData.scorePlayer1.toString()
        findViewById<TextView>(R.id.tvCurrentPointsPlayer2).text = gameData.scorePlayer2.toString()
    }

    private fun loadRandomQuestion(categoryName: String) {
        val sessionManager = SessionManager(this)
        val api = ApiClient.getClientService(this)

        Log.d("CATEGORY_ID_DEBUG", "Buscando preguntas de categoría ID: $categoryName")

        api.getRandomQuestionsByCategory(categoryName).enqueue(object : Callback<Question> {
                override fun onResponse(call: Call<Question>, response: Response<Question>) {
                    if (response.isSuccessful) {
                        val question = response.body()
                        if (question == null) {
                            Toast.makeText(
                                this@QuestionActivity,
                                "No hay pregunta disponible",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.d("QUESTION_RECEIVED", "Pregunta: $question")

                            tvQuestion.text = question.statement
                            correctAnswer = question.correctOption

                            if (!question.imageUrl.isNullOrEmpty()) {
                                Glide.with(this@QuestionActivity).load(question.imageUrl)
                                    .into(imgQuestion)
                            } else {
                                imgQuestion.setImageResource(R.drawable.ic_launcher_background)
                            }

                            val answers = listOfNotNull(
                                question.correctOption,
                                question.wrongOption1,
                                question.wrongOption2,
                                question.wrongOption3
                            ).shuffled()

                            val buttons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)

                            for (i in buttons.indices) {
                                if (i < answers.size) {
                                    buttons[i].text = answers[i]
                                    buttons[i].isEnabled = true
                                    buttons[i].isVisible = true
                                    buttons[i].setOnClickListener { checkAnswer(buttons[i]) }
                                } else {
                                    buttons[i].isEnabled = false
                                    buttons[i].isVisible = false
                                }
                            }
                        }
                    } else {
                        Log.e("QUESTION_DEBUG", "Error al cargar pregunta: ${response.code()}")
                        Toast.makeText(
                            this@QuestionActivity,
                            "Error cargando pregunta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Question>, t: Throwable) {
                    Log.e("QUESTION_DEBUG", "Fallo de conexión: ${t.localizedMessage}")
                    Toast.makeText(this@QuestionActivity, "Fallo de conexión", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun checkAnswer(selectedButton: Button) {
        //elimina el contador cuando se responde
        countdownRunnable?.let { handler.removeCallbacks(it) }

        val selectedAnswer = selectedButton.text.toString()
        Log.d("ANSWER_SELECTED", "Respuesta seleccionada: $selectedAnswer")
        Log.d("ANSWER_CORRECT", "Respuesta correcta: $correctAnswer")

        val buttons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        buttons.forEach { it.setOnClickListener(null) }
        withdraw.setOnClickListener { null }

        if (selectedAnswer == correctAnswer) {
            playSound(R.raw.correct_answer)
            handler.postDelayed({

                if (usuarioLogueado.username == game?.player1?.username) {
                    game?.scorePlayer1 = (game?.scorePlayer1 ?: 0) + 1
                    if (game?.scorePlayer1 == 5) {
                        game?.starsPlayer1 = (game?.starsPlayer1 ?: 0) + 1
                        game?.scorePlayer1 = 0
                    }

                    if (game?.starsPlayer1 == 3) {
                        game?.turn = game?.player2
                        updateGame(game!!)
                        navigateTo(GamesActivity::class.java)
                        return@postDelayed
                    } else {
                        game?.turn = game?.player2
                        updateGame(game!!)
                        navigateTo(CategoryActivity::class.java)
                        return@postDelayed
                    }

                } else {
                    game?.scorePlayer2 = (game?.scorePlayer2 ?: 0) + 1
                    if (game?.scorePlayer2 == 5) {
                        game?.starsPlayer2 = (game?.starsPlayer2 ?: 0) + 1
                        game?.scorePlayer2 = 0
                    }

                    if (game?.starsPlayer2 == 3) {
                        game?.turn = null
                        game?.status = Game.Status.FINISHED

                        if ((game?.starsPlayer1 ?: 0) < 3) {
                            // Solo jugador 2 tiene 3 estrellas: gana
                            game?.winner = game?.player2!!
                        }
                        // Si ambos tienen 3 estrellas: empate (winner sigue siendo null por defecto)

                        updateGame(game!!)
                        navigateTo(GamesActivity::class.java)
                        return@postDelayed
                    } else {
                        game?.turn = game?.player1
                        updateGame(game!!)
                        navigateTo(CategoryActivity::class.java)
                        return@postDelayed
                    }
                }
            }, 2000)
        } else {
            playSound(R.raw.wrong_answer)
            handler.postDelayed({
                if (game?.starsPlayer1 == 3) {
                    game?.winner = game?.player1!!
                } else {
                    game?.turn =
                        if (usuarioLogueado.username == game?.player1?.username) game?.player2 else game?.player1
                }
                updateGame(game!!)
                navigateTo(GamesActivity::class.java)
            }, 2000)
        }
    }

    private fun navigateTo(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        intent.putExtra("SELECTED_GAME", game)
        startActivity(intent)
        finish()
    }


    private fun updateGame(game: Game) {
        val api = ApiClient.getGameService(this)

        Log.d("CATEGORY_ID_DEBUG", "Haciendo update del game: $game")

        api.updateGame(game).enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
                    Log.d("CATEGORY_ID_DEBUG", "Update hecho correctamente: $game")
                    // Puedes hacer algo más aquí, como actualizar UI o notificar al usuario.
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "QUESTION_DEBUG",
                        "Error al actualizar el juego: ${response.code()} - ${errorBody}"
                    )
                    Toast.makeText(
                        this@QuestionActivity, "Error actualizando el juego", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Game>, t: Throwable) {
                Log.e("QUESTION_DEBUG", "Fallo de conexión: ${t.localizedMessage}")
                Toast.makeText(this@QuestionActivity, "Fallo de conexión", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun startCountdown() {
        tvSecondsRemaining.text = timeRemaining.toString()

        countdownRunnable = object : Runnable {
            override fun run() {
                timeRemaining--
                tvSecondsRemaining.text = timeRemaining.toString()

                if (timeRemaining > 0) {
                    handler.postDelayed(this, 1000)
                } else {
                    handleTimeout()
                }
            }
        }

        handler.postDelayed(countdownRunnable!!, 1000)
    }

    private fun handleTimeout() {
        //desactiva botones de respuesta
        val buttons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        buttons.forEach {
            it.setOnClickListener(null)
        }

        if (game?.player1?.username == game?.turn?.username) {
            game?.turn = game?.player2
        } else {
            game?.turn = game?.player1
        }

        game?.let { updateGame(it) }

        val intent = Intent(this, GamesActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playSound(resId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        countdownRunnable?.let { handler.removeCallbacks(it) }
    }

}
