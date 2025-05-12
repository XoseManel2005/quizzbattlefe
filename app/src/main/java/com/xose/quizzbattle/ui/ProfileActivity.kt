package com.xose.quizzbattle.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var usuarioLogueado: User
    private lateinit var imgProfilePic: ImageView
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usuarioLogueado = SessionManager(this).getLoggedUser() as User
        imgProfilePic = findViewById(R.id.imgProfilePic)

        // Registrar el callback aquí, donde ya imgProfilePic está inicializado
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgProfilePic.setImageURI(uri)

                // Convertir URI a Base64
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val imageBytes = outputStream.toByteArray()
                    val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                    Log.d("Base64", imageBase64)
                }
            } else {
                // Imagen no seleccionada
                Log.d("Base64", "Imagen no seleccionada")
            }
        }

        imgProfilePic.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        val tvLogout = findViewById<TextView>(R.id.tvLogout)
        val btnAddFriend = findViewById<Button>(R.id.btnAddFriend)
        val imgGames = findViewById<ImageView>(R.id.imgGames)
        val imgFriendship = findViewById<ImageView>(R.id.imgFriendships)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)

        tvUsername.text = usuarioLogueado.username
        tvEmail.text = usuarioLogueado.email ?: "Email no disponible"

        val tvFriends = findViewById<TextView>(R.id.tvFriends)

        val tvWonGames = findViewById<TextView>(R.id.tvWonGames)

        val tvTotalGames = findViewById<TextView>(R.id.tvTotalGames)

        lifecycleScope.launch {
            try {
                val gameService = ApiClient.getGameService(this@ProfileActivity)

                //recoger todas las amistades
                val friendships = gameService.getAcceptedFriendships(usuarioLogueado.username)
                tvFriends.text = friendships.size.toString()

                //obtener partidas terminadas
                val finishedGames = gameService.getGames(usuarioLogueado.username, "FINISHED")

                //ver partidas ganadas
                val wonGames = finishedGames.count { game ->
                    game.winner != null && game.winner.id == usuarioLogueado.id
                }
                tvWonGames.text = wonGames.toString()

                //obtener partidas en curso
                val ongoingGames = gameService.getGames(usuarioLogueado.username, "ONGOING")

                //sumar partidas en curso y acabadas
                val allGames = finishedGames + ongoingGames

                //las contamos y mostramos como total de partidas
                val totalGames = allGames.count { game ->
                    game.player1.id == usuarioLogueado.id || game.player2.id == usuarioLogueado.id
                }
                tvTotalGames.text = totalGames.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                tvFriends.text = "0"
                tvWonGames.text = "0"
                tvTotalGames.text = "0"
            }
        }

        val text = "Cerrar Sesión"
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, 0)

        tvLogout.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light)) // Usa tu color rojo personalizado si tienes
        tvLogout.text = spannable

        tvLogout.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAddFriend.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgGames.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgFriendship.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}