package com.xose.quizzbattle.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.xose.quizzbattle.R

class QuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val withdraw = findViewById<Button>(R.id.btnWithdraw)

        withdraw.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
        }
    }
}