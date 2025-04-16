package com.xose.quizzbattle.ui

import GameAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.data.GameService
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch

class GamesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        recyclerView = findViewById(R.id.rvPartidas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioLogueado = SessionManager(this).getLoggedUser() ?: return
        gameService = ApiClient.getGameService(this)

        lifecycleScope.launch {
            try {
                val games = gameService.getGames(username = usuarioLogueado.username)
                adapter = GameAdapter(games, usuarioLogueado) { selectedGame ->
                    Log.d("LOAD_GAMES", "${selectedGame.toString()}")
                }
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GamesActivity, "Error al cargar partidas: ${e.message}", Toast.LENGTH_LONG).show()
                Log.d("LOAD_GAMES", "Error al cargar partidas: ${e.message}")
            }
        }
    }
}
