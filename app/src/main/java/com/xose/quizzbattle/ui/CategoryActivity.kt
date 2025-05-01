package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Category
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryActivity : AppCompatActivity() {

    private val localCategoryImages = listOf(
        R.drawable.category_science,
        R.drawable.category_sports,
        R.drawable.category_cinema,
        R.drawable.category_history
    )

    private val localToNameMap = mapOf(
        R.drawable.category_science to "Ciencia",
        R.drawable.category_sports to "Deportes",
        R.drawable.category_cinema to "Entretenimiento",
        R.drawable.category_history to "Historia"
    )

    private var finalCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val game = intent.getSerializableExtra("SELECTED_GAME") as? Game
        game?.let { gameData ->
            // Actualizar estrellas del jugador 1
            val starsPlayer1 = listOf(
                findViewById<ImageView>(R.id.imgStar1Player1),
                findViewById<ImageView>(R.id.imgStar2Player1),
                findViewById<ImageView>(R.id.imgStar3Player1)
            )

            for (i in 0 until gameData.starsPlayer1.coerceAtMost(3)) {
                starsPlayer1[i].setImageResource(R.drawable.baseline_star_on)
            }

            // Actualizar estrellas del jugador 2
            val starsPlayer2 = listOf(
                findViewById<ImageView>(R.id.imgStar1Player2),
                findViewById<ImageView>(R.id.imgStar2Player2),
                findViewById<ImageView>(R.id.imgStar3Player2)
            )

            for (i in 0 until gameData.starsPlayer2.coerceAtMost(3)) {
                starsPlayer2[i].setImageResource(R.drawable.baseline_star_on)
            }

            // Actualizar puntos
            findViewById<TextView>(R.id.tvCurrentPointsPlayer1).text = gameData.scorePlayer1.toString()
            findViewById<TextView>(R.id.tvCurrentPointsPlayer2).text = gameData.scorePlayer2.toString()
        }

        val imgCategory = findViewById<ImageView>(R.id.imgQuestion)
        val withdraw = findViewById<Button>(R.id.btnWithdraw)

        withdraw.setOnClickListener {
            if (game?.player1?.username ?: null  == game?.turn?.username ?: null){
                game?.turn = game?.player2
            } else {
                game?.turn = game?.player1
            }
            game?.let { it1 -> updateGame(it1) }


            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadCategories(imgCategory)
    }

    private fun loadCategories(imageView: ImageView) {
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        val api = ApiClient.getClientService(this)

        api.getCategories("Bearer $token").enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    if (categories.isNullOrEmpty()) {
                        Log.w("CATEGORIES_DEBUG", "La lista de categorías está vacía.")
                    } else {
                        for (cat in categories) {
                            Log.d("CATEGORIES_DEBUG", "ID: ${cat.id}, Categoría: ${cat.name}, URL: ${cat.imageUrl}")
                        }
                        startImageRoulette(imageView, categories)
                    }
                } else {
                    Log.e("CATEGORIES_DEBUG", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@CategoryActivity, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startImageRoulette(imageView: ImageView, categories: List<Category>) {
        var index = 0
        var interval: Long = 70
        val handler = android.os.Handler(mainLooper)
        val totalDuration = 4000L
        val startTime = System.currentTimeMillis()

        val roulette = object : Runnable {
            override fun run() {
                imageView.setImageResource(localCategoryImages[index % localCategoryImages.size])
                index++

                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < totalDuration) {
                    interval += 30
                    handler.postDelayed(this, interval)
                } else {
                    // Elegir aleatoriamente una imagen local final
                    val finalDrawableId = localCategoryImages.random()
                    imageView.setImageResource(finalDrawableId)

                    // Buscar el nombre asociado a esa imagen
                    val localName = localToNameMap[finalDrawableId]?.trim()

                    // Buscar la categoría correspondiente en la lista de la API
                    finalCategory = categories.find { it.name.trim().equals(localName, ignoreCase = true) }

                    findViewById<TextView>(R.id.tvCategory).text =
                        "Categoría: ${finalCategory?.name ?: "Desconocida"}"

                    // Esperar 2 segundos y lanzar a QuestionActivity
                    handler.postDelayed({
                        finalCategory?.let { category ->
                            val game = intent.getSerializableExtra("SELECTED_GAME") as? Game
                            game?.let {
                                val intent = Intent(this@CategoryActivity, QuestionActivity::class.java)
                                intent.putExtra("SELECTED_GAME", it)
                                intent.putExtra("SELECTED_CATEGORY", category)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }, 2000)
                }
            }
        }

        handler.post(roulette)
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
                    Log.e("QUESTION_DEBUG", "Error al actualizar el juego: ${response.code()} - ${errorBody}")
                    Toast.makeText(
                        this@CategoryActivity,
                        "Error actualizando el juego",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Game>, t: Throwable) {
                Log.e("QUESTION_DEBUG", "Fallo de conexión: ${t.localizedMessage}")
                Toast.makeText(this@CategoryActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}