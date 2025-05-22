package com.xose.quizzbattle.ui

import FinishedGameAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private lateinit var tvNoFinishedGames: TextView
    private lateinit var adapterFinished: FinishedGameAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_finished, container, false)

        recyclerViewFinished = view.findViewById(R.id.rvFriends)
        recyclerViewFinished.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutFinished)
        swipeRefreshLayout.setOnRefreshListener {
            loadFinishedGames()
        }

        tvNoFinishedGames =  view.findViewById(R.id.tvNoFinishedGames)
        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        loadFinishedGames()

        return view
    }

    private fun loadFinishedGames() {
        viewLifecycleOwner.lifecycleScope.launch {
            swipeRefreshLayout.isRefreshing = true
            try {
                val games = gameService.getGames(usuarioLogueado.username, Game.Status.FINISHED.toString())
                if (games.isNullOrEmpty()){
                    tvNoFinishedGames.visibility = View.VISIBLE
                }
                adapterFinished = FinishedGameAdapter(games, usuarioLogueado)
                recyclerViewFinished.adapter = adapterFinished
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar partidas", Toast.LENGTH_LONG).show()
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

}
