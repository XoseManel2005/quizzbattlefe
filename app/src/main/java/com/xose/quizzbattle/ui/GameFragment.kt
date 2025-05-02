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

        viewLifecycleOwner.lifecycleScope.launch {
            var userGames = mutableListOf<Game>()
            var rivalGames = mutableListOf<Game>()
            val games = gameService.getGames(usuarioLogueado.username, Game.Status.ONGOING.toString())
            games.forEach { game ->
                if (game.turn?.username == usuarioLogueado.username) {
                    userGames.add(game)
                } else {
                    rivalGames.add(game)
                }
            }
            try {

                adapter = GameAdapter(userGames, usuarioLogueado) { selectedGame ->
                    val intent = Intent(requireContext(), CategoryActivity::class.java)
                    intent.putExtra("SELECTED_GAME", selectedGame) // ahora sí funcionará
                    startActivity(intent)
                    Log.d("LOAD_GAMES", "$selectedGame")
                    requireActivity().finish()
                }
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar partidas", Toast.LENGTH_LONG).show()
            }

            try {
                adapter = GameAdapter(rivalGames, usuarioLogueado) {
                    Log.d("LOAD_GAMES", "$it")
                }
                recyclerViewRival.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar partidas", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }
}
