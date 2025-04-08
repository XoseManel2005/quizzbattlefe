package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.xose.quizzbattle.R

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val imgCategory = findViewById<ImageView>(R.id.imgSelectedCategory)


        imgCategory.setOnClickListener {
            val intent = Intent (this, QuestionActivity::class.java)
            startActivity(intent)
        }
    }
}