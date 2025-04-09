package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xose.quizzbattle.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterIntent = findViewById<TextView>(R.id.tvGenericText)
        val text = getString(R.string.register_link)
        val spannable = SpannableString(text)

        spannable.setSpan(UnderlineSpan(), 0, text.length, 0)
        tvRegisterIntent.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        tvRegisterIntent.text = spannable

        tvRegisterIntent.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

    }
}