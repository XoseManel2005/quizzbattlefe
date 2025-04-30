package com.xose.quizzbattle.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.data.GameService
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GamesActivity : AppCompatActivity() {
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        val btnOngoing = findViewById<Button>(R.id.btnAmistades)
        val btnFinished = findViewById<Button>(R.id.btnSolicitudes)
        val imgFriendships = findViewById<ImageView>(R.id.imgFriendships)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val imgGame = findViewById<ImageView>(R.id.imgGames)
        usuarioLogueado = SessionManager(this).getLoggedUser()!!



        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, GamesFragment())
            .commit()


        val colorSelected = Color.parseColor("#56000000") // más oscuro
        val colorUnselected = Color.parseColor("#00000000") // transparente

        // Cambiar colores
        btnFinished.backgroundTintList = ColorStateList.valueOf(colorSelected)
        btnOngoing.backgroundTintList = ColorStateList.valueOf(colorUnselected)
        btnOngoing.isEnabled = false

        btnOngoing.setOnClickListener {
            btnFinished.isEnabled = true
            // Cambiar colores
            btnFinished.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnOngoing.backgroundTintList = ColorStateList.valueOf(colorUnselected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, GamesFragment())
                .commit()
        }

        btnFinished.setOnClickListener {
            btnOngoing.isEnabled = true
            // Cambiar colores
            btnOngoing.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnFinished.backgroundTintList = ColorStateList.valueOf(colorUnselected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FinishedGameFragment())
                .commit()
        }

        imgFriendships.setOnClickListener{
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()

            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgProfile.setOnClickListener {
            val intent = Intent (this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgGame.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres crear una nueva partida?")
                .setPositiveButton("Sí") { _, _ ->
                    gameService = ApiClient.getGameService(this)

                    val call = gameService.createRandomGame(usuarioLogueado.username)

                    call.enqueue(object : Callback<Game> {
                        override fun onResponse(call: Call<Game>, response: Response<Game>) {
                            if (response.isSuccessful) {
                                val game = response.body()
                                if (game != null) {
                                    val intent = Intent(this@GamesActivity, CategoryActivity::class.java)
                                    intent.putExtra("SELECTED_GAME", game) // Game debe implementar Serializable o Parcelable
                                    startActivity(intent)
                                    Log.d("LOAD_GAMES", "$game")
                                } else {
                                    Log.e("LOAD_GAMES", "Respuesta sin cuerpo")
                                }
                            } else {
                                Log.e("LOAD_GAMES", "Respuesta no exitosa: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Game>, t: Throwable) {
                            Log.e("LOAD_GAMES", "Error en la llamada a la API", t)
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


    }
}
