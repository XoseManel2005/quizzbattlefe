package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xose.quizzbattle.R
import com.xose.quizzbattle.util.SessionManager

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvLogout = findViewById<TextView>(R.id.tvLogout)
        val btnAddFriend = findViewById<Button>(R.id.btnAddFriend)
        val imgGames = findViewById<ImageView>(R.id.imgGames)
        val imgFriendship = findViewById<ImageView>(R.id.imgFriendships)

        val text = "Cerrar Sesión"
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, 0)

        val tvCerrarSesion = findViewById<TextView>(R.id.tvLogout)
        tvCerrarSesion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light)) // Usa tu color rojo personalizado si tienes
        tvCerrarSesion.text = spannable

        tvLogout.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnAddFriend.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }

        imgGames.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
        }

        imgFriendship.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }



    }
}