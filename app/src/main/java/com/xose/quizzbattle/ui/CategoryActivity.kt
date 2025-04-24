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
        game?.let {
            Log.d("CATEGORY_ACTIVITY", "Recibido game: $game")
        }

        val imgCategory = findViewById<ImageView>(R.id.imgQuestion)
        val withdraw = findViewById<Button>(R.id.btnWithdraw)


        imgCategory.setOnClickListener {
            finalCategory?.let {
                val intent = Intent(this, QuestionActivity::class.java)
                intent.putExtra("selectedCategoryId", it.id)
                intent.putExtra("selectedCategoryName", it.name)
                startActivity(intent)
            }
        }

        withdraw.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
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
                }
            }
        }

        handler.post(roulette)

        findViewById<ImageView>(R.id.imgQuestion).setOnClickListener {
            finalCategory?.let {
                val intent = Intent(this, QuestionActivity::class.java)
                intent.putExtra("selectedCategoryId", it.id)
                intent.putExtra("selectedCategoryName", it.name)
                startActivity(intent)
            }
        }
    }
}