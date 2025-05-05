package com.xose.quizzbattle.ui

import FriendsAddAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.data.GameService
import com.xose.quizzbattle.model.Friendship
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class AddFriendFragment : Fragment() {
    private lateinit var rvPlayers: RecyclerView
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    private lateinit var searchView: SearchView
    private var searchList = arrayListOf<User>()
    private lateinit var adapter: FriendsAddAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)

        rvPlayers = view.findViewById(R.id.rvPlayers)
        searchView = view.findViewById(R.id.search)
        rvPlayers.layoutManager = LinearLayoutManager(requireContext())

        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        loadGames()

        return view
    }

    private fun loadGames() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val players = gameService.getAllPlayers(User.Role.PLAYER.toString())
                val updatedRequests = gameService.getPendingFriendRequests(
                    usuarioLogueado.username,
                    Friendship.Status.PENDING.toString()
                )
                val friends = gameService.getAcceptedFriendships(usuarioLogueado.username)

                // Filtra los jugadores que tienen una solicitud pendiente con el usuario logueado
                val notFriends = players.filter { player ->
                    player.username != usuarioLogueado.username &&
                            updatedRequests.none { request ->
                                request.sender.username == player.username || request.receiver.username == player.username
                            } &&
                            friends.none { friendship ->
                                friendship.sender.username == player.username || friendship.receiver.username == player.username
                            }
                }


                println("Total players: ${players.size}")
                println("Pending requests: ${updatedRequests.size}")
                println("Not friends: ${notFriends.size}")

                searchList.clear()
                searchList.addAll(notFriends)

                adapter = FriendsAddAdapter(searchList) { selectedPlayer ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirmación")
                        .setMessage("¿Estás seguro de que quieres agregar este jugador?")
                        .setPositiveButton("Sí") { _, _ ->
                            val call = gameService.createFriendship(usuarioLogueado.username, selectedPlayer.username)
                            call.enqueue(object : Callback<Friendship> {
                                override fun onResponse(call: Call<Friendship>, response: Response<Friendship>) {
                                    if (response.isSuccessful) {
                                        loadGames()
                                        Log.e("LOAD_FRIENDSHIP", "Respuesta sin cuerpo")
                                    } else {
                                        Log.e("LOAD_FRIENDSHIP", "Respuesta no exitosa: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Friendship>, t: Throwable) {
                                    Log.e("LOAD_FRIENDSHIP", "Error en la llamada a la API", t)
                                }
                            })
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                rvPlayers.adapter = adapter

                searchView.clearFocus()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        val filteredList = if (!newText.isNullOrEmpty()) {
                            notFriends.filter {
                                it.username.lowercase(Locale.getDefault())
                                    .contains(newText.lowercase(Locale.getDefault()))
                            }
                        } else {
                            notFriends
                        }

                        searchList.clear()
                        searchList.addAll(filteredList)
                        adapter.notifyDataSetChanged()

                        return false
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Error al cargar jugadores",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
