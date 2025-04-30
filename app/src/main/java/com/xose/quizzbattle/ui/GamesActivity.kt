package com.xose.quizzbattle.ui

import FinishedGameAdapter
import GameAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.xose.quizzbattle.R
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager

class GamesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        val btnOngoing = findViewById<Button>(R.id.btnOngoing)
        val btnFinished = findViewById<Button>(R.id.btnFinished)
        val imgFriendships = findViewById<ImageView>(R.id.imgFriendships)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)

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
    }
}
