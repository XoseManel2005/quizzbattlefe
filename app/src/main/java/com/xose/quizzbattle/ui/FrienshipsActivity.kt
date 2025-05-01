package com.xose.quizzbattle.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.util.SessionManager

class FrienshipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frienships)


        val btnAmistades = findViewById<Button>(R.id.btnAmistades)
        val btnSolicitudes = findViewById<Button>(R.id.btnSolicitudes)
        val imgFriendships = findViewById<ImageView>(R.id.imgFriendships)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, GamesFragment())
            .commit()


        val colorSelected = Color.parseColor("#56000000") // más oscuro
        val colorUnselected = Color.parseColor("#00000000") // transparente

        // Cambiar colores
        btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorSelected)
        btnAmistades.backgroundTintList = ColorStateList.valueOf(colorUnselected)
        btnAmistades.isEnabled = false

        btnAmistades.setOnClickListener {
            btnSolicitudes.isEnabled = true
            // Cambiar colores
            btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnAmistades.backgroundTintList = ColorStateList.valueOf(colorUnselected)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, GamesFragment())
                .commit()
        }

        btnSolicitudes.setOnClickListener {
            btnAmistades.isEnabled = true
            // Cambiar colores
            btnAmistades.backgroundTintList = ColorStateList.valueOf(colorSelected)
            btnSolicitudes.backgroundTintList = ColorStateList.valueOf(colorUnselected)

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