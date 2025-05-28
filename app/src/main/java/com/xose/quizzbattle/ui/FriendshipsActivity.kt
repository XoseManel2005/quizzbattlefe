package com.xose.quizzbattle.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.data.GameService
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch

class FriendshipsActivity : AppCompatActivity() {
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frienships)


        val btnAmistades = findViewById<Button>(R.id.btnAmistades)
        val imgGames = findViewById<ImageView>(R.id.imgGames)
        val btnSolicitudes = findViewById<Button>(R.id.btnSolicitudes)
        val imgFriendships = findViewById<ImageView>(R.id.imgFriendships)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val imgAddFriends = findViewById<ImageView>(R.id.imgAddFriends)
        usuarioLogueado = SessionManager(this).getLoggedUser()!!

        lifecycleScope.launch {
            try {
                val gameService = ApiClient.getGameService(this@FriendshipsActivity)
                val profileImage = gameService.getProfileImage(usuarioLogueado.username)
                if (profileImage.imageBase64 != null || !profileImage.imageBase64.isEmpty()) {
                    try {
                        // 1. Eliminar el prefijo si existe
                        val base64Image = profileImage.imageBase64.substringAfter(
                            "base64,",
                            profileImage.imageBase64
                        )

                        // 2. Decodificar a bytes
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

                        // 3. Convertir a Bitmap
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // 4. Asignar al ImageView
                        imgProfile.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Base64", "Error al convertir la imagen Base64: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.gamesContainer, FriendsFragment())
            .commit()


        val colorSelected = Color.parseColor("#CBE5FF")
        val colorUnselected = Color.parseColor("#4A90E2")

        // Cambiar colores
        btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorSelected)
        btnAmistades.backgroundTintList = ColorStateList.valueOf(colorUnselected)
        btnAmistades.isEnabled = false
        imgAddFriends.isVisible = true

        btnAmistades.setOnClickListener {
            btnSolicitudes.isEnabled = true
            imgAddFriends.isVisible = true
            // Cambiar colores
            btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnAmistades.backgroundTintList = ColorStateList.valueOf(colorUnselected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.gamesContainer, FriendsFragment())
                .commit()
        }

        imgAddFriends.setOnClickListener {
            btnSolicitudes.isEnabled = true
            btnAmistades.isEnabled = true
            imgAddFriends.isVisible = false
            // Cambiar colores
            btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnAmistades.backgroundTintList = ColorStateList.valueOf(colorSelected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.gamesContainer, AddFriendFragment())
                .commit()
        }

        btnSolicitudes.setOnClickListener {
            btnAmistades.isEnabled = true
            imgAddFriends.isVisible = true
            // Cambiar colores
            btnAmistades.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorUnselected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.gamesContainer, FriendsRequestFragment())
                .commit()
        }


        imgProfile.setOnClickListener {
            val intent = Intent (this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgGames.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}