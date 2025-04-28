package com.xose.quizzbattle.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Category
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.Question
import com.xose.quizzbattle.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val withdraw = findViewById<Button>(R.id.btnWithdraw)

        withdraw.setOnClickListener {
            val intent = Intent(this, GamesActivity::class.java)
            startActivity(intent)
        }

        tvQuestion = findViewById(R.id.tvQuestion)
        imgQuestion = findViewById(R.id.imgQuestion)
        btnAnswer1 = findViewById(R.id.btnAnswer1)
        btnAnswer2 = findViewById(R.id.btnAnswer2)
        btnAnswer3 = findViewById(R.id.btnAnswer3)
        btnAnswer4 = findViewById(R.id.btnAnswer4)

        val game = intent.getSerializableExtra("SELECTED_GAME") as? Game
        val category = intent.getSerializableExtra("SELECTED_CATEGORY") as? Category

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
            findViewById<ImageView>(R.id.imgStar2Player2),
            findViewById<ImageView>(R.id.imgStar3Player2)
        )

        for (i in 0 until gameData.starsPlayer2.coerceAtMost(3)) {
            starsPlayer2[i].setImageResource(R.drawable.baseline_star_on)
        }

        findViewById<TextView>(R.id.tvCurrentPointsPlayer1).text = gameData.scorePlayer1.toString()
        findViewById<TextView>(R.id.tvCurrentPointsPlayer2).text = gameData.scorePlayer2.toString()
    }

    private fun loadRandomQuestion(categoryName: String) {
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        val api = ApiClient.getClientService(this)

        Log.d("CATEGORY_ID_DEBUG", "Buscando preguntas de categoría ID: $categoryName")
        Log.d("CATEGORY_ID_DEBUG", "Categoria enviada: '${categoryName}'")

        api.getRandomQuestionsByCategory(categoryName)
            .enqueue(object : Callback<Question> {
                override fun onResponse(call: Call<Question>, response: Response<Question>) {
                    if (response.isSuccessful) {
                        val question = response.body()
                        if (question == null) {
                            Toast.makeText(this@QuestionActivity, "No hay pregunta disponible", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("QUESTION_RECEIVED", "Pregunta: $question")

                            tvQuestion.text = question.statement

                            if (!question.imageUrl.isNullOrEmpty()) {
                                Glide.with(this@QuestionActivity)
                                    .load(question.imageUrl)
                                    .into(imgQuestion)
                            } else {
                                imgQuestion.setImageResource(R.drawable.ic_launcher_background)
                            }

                            val answers = listOf(
                                question.wrongOption1,
                                question.wrongOption2,
                                question.wrongOption3,
                                question.correctOption
                            ).shuffled()

                            btnAnswer1.text = answers[0]
                            btnAnswer2.text = answers[1]
                            btnAnswer3.text = answers[2]
                            btnAnswer4.text = answers[3]
                        }
                    } else {
                        Log.e("QUESTION_DEBUG", "Error al cargar pregunta: ${response.code()}")
                        Toast.makeText(this@QuestionActivity, "Error cargando pregunta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Question>, t: Throwable) {
                    Log.e("QUESTION_DEBUG", "Fallo de conexión: ${t.localizedMessage}")
                    Toast.makeText(this@QuestionActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

