package com.xose.quizzbattle.ui

import FinishedGameAdapter
import android.os.Bundle
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

class FinishedGameFragment : Fragment() {
    private lateinit var recyclerViewFinished: RecyclerView
    private lateinit var adapterFinished: FinishedGameAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_finished, container, false)

        recyclerViewFinished = view.findViewById(R.id.rvFriends)
        recyclerViewFinished.layoutManager = LinearLayoutManager(requireContext())

        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val games = gameService.getGames(usuarioLogueado.username, Game.Status.FINISHED.toString())
                adapterFinished = FinishedGameAdapter(games, usuarioLogueado)
                recyclerViewFinished.adapter = adapterFinished
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar partidas", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }
}
