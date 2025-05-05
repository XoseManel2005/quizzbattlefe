package com.xose.quizzbattle.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import com.xose.quizzbattle.R
import com.xose.quizzbattle.util.SessionManager

class FrienshipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frienships)


        val btnAmistades = findViewById<Button>(R.id.btnAmistades)
        val imgGames = findViewById<ImageView>(R.id.imgGames)
        val btnSolicitudes = findViewById<Button>(R.id.btnSolicitudes)
        val imgFriendships = findViewById<ImageView>(R.id.imgFriendships)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val imgAddFriends = findViewById<ImageView>(R.id.imgAddFriends)

        supportFragmentManager.beginTransaction()
            .replace(R.id.gamesContainer, FriendsFragment())
            .commit()


        val colorSelected = Color.parseColor("#56000000") // más oscuro
        val colorUnselected = Color.parseColor("#00000000") // transparente

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