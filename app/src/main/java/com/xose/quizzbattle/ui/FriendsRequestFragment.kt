package com.xose.quizzbattle.ui

import FriendsAdapter
import FriendsRequestAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class FriendsRequestFragment : Fragment() {
    private lateinit var recyclerViewFriends: RecyclerView
    private lateinit var adapterFriends: FriendsRequestAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User
    private lateinit var tvNoFriendRequests: TextView // Referencia al TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends_request, container, false)

        recyclerViewFriends = view.findViewById(R.id.rvFriends)
        recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())

        // Referencia al TextView que muestra el mensaje de "No solicitudes"
        tvNoFriendRequests = view.findViewById(R.id.tvNoRequests)

        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val pendingRequests = gameService.getPendingFriendRequests(usuarioLogueado.username, Friendship.Status.PENDING.toString()) // Este endpoint debe existir

                if (pendingRequests.isEmpty()) {
                    // Si no hay solicitudes, muestra el mensaje y oculta el RecyclerView
                    tvNoFriendRequests.visibility = View.VISIBLE
                    recyclerViewFriends.visibility = View.GONE
                } else {
                    // Si hay solicitudes, muestra el RecyclerView
                    tvNoFriendRequests.visibility = View.GONE
                    recyclerViewFriends.visibility = View.VISIBLE

                    adapterFriends = FriendsRequestAdapter(
                        pendingRequests,
                        usuarioLogueado,
                        onAcceptClick = { friendship ->
                            lifecycleScope.launch {
                                try {
                                    gameService.acceptFriendship(friendship.id)
                                    Toast.makeText(requireContext(), "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                                    refreshFriendRequests()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(requireContext(), "Error al aceptar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDenyClick = { friendship ->
                            lifecycleScope.launch {
                                try {
                                    val response = gameService.denyFriendship(friendship.id)
                                    if (response.isSuccessful) {
                                        Toast.makeText(requireContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                                        refreshFriendRequests()
                                    } else {
                                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(requireContext(), "Error al rechazar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                    recyclerViewFriends.adapter = adapterFriends
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar solicitudes", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun refreshFriendRequests() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val updatedRequests = gameService.getPendingFriendRequests(usuarioLogueado.username, Friendship.Status.PENDING.toString())

                if (updatedRequests.isEmpty()) {
                    // Si no hay solicitudes después de actualizar, muestra el mensaje
                    tvNoFriendRequests.visibility = View.VISIBLE
                    recyclerViewFriends.visibility = View.GONE
                } else {
                    // Si hay solicitudes después de actualizar, muestra el RecyclerView
                    tvNoFriendRequests.visibility = View.GONE
                    recyclerViewFriends.visibility = View.VISIBLE

                    adapterFriends = FriendsRequestAdapter(
                        updatedRequests,
                        usuarioLogueado,
                        onAcceptClick = { friendship -> /* Lógica para aceptar */ },
                        onDenyClick = { friendship -> /* Lógica para rechazar */ }
                    )
                    recyclerViewFriends.adapter = adapterFriends
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}