package com.xose.quizzbattle.ui

import GameAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.data.GameService
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch

class GamesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewRival: RecyclerView
    private lateinit var adapter: GameAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        recyclerViewRival = view.findViewById(R.id.rvFriends)
        recyclerViewRival.layoutManager = LinearLayoutManager(requireContext())

        recyclerView = view.findViewById(R.id.rvPartidas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            loadGames()
        }

        // Cargar datos solo una vez desde la función correcta
        loadGames()

        return view
    }


    private fun loadGames() {
        viewLifecycleOwner.lifecycleScope.launch {
            swipeRefreshLayout.isRefreshing = true
            try {
                val games = gameService.getGames(usuarioLogueado.username, Game.Status.ONGOING.toString())
                val userGames = games.filter { it.turn?.username == usuarioLogueado.username }
                val rivalGames = games.filter { it.turn?.username != usuarioLogueado.username }

                recyclerView.adapter = GameAdapter(userGames, usuarioLogueado) { selectedGame ->
                    val intent = Intent(requireContext(), CategoryActivity::class.java)
                    intent.putExtra("SELECTED_GAME", selectedGame)
                    startActivity(intent)
                    requireActivity().finish()
                }

                recyclerViewRival.adapter = GameAdapter(rivalGames, usuarioLogueado) {
                    // Acción opcional
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar partidas", Toast.LENGTH_LONG).show()
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }


}
