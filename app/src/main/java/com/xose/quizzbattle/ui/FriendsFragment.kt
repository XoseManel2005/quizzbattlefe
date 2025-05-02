package com.xose.quizzbattle.ui

import FriendsAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsFragment : Fragment() {
    private lateinit var recyclerViewFriends: RecyclerView
    private lateinit var adapterFriends: FriendsAdapter
    private lateinit var gameService: GameService
    private lateinit var usuarioLogueado: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        recyclerViewFriends = view.findViewById(R.id.rvFriends)
        recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())

        usuarioLogueado = SessionManager(requireContext()).getLoggedUser() ?: return view
        gameService = ApiClient.getGameService(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val friends = gameService.getAcceptedFriendships(usuarioLogueado.username)
                adapterFriends = FriendsAdapter(friends, usuarioLogueado) { selectedFriend ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirmación")
                        .setMessage("¿Estás seguro de que quieres crear una nueva partida?")
                        .setPositiveButton("Sí") { _, _ ->
                            gameService = ApiClient.getGameService(requireContext())

                            val call = gameService.createRandomGame(usuarioLogueado.username, selectedFriend.username)

                            call.enqueue(object : Callback<Game> {
                                override fun onResponse(call: Call<Game>, response: Response<Game>) {
                                    if (response.isSuccessful) {
                                        val game = response.body()
                                        if (game != null) {
                                            val intent = Intent(requireContext(), CategoryActivity::class.java)
                                            intent.putExtra("SELECTED_GAME", game) // Game debe implementar Serializable o Parcelable
                                            startActivity(intent)
                                            requireActivity().finish()
                                            Log.d("LOAD_GAMES", "$game")
                                        } else {
                                            Log.e("LOAD_GAMES", "Respuesta sin cuerpo")
                                        }
                                    } else {
                                        Log.e("LOAD_GAMES", "Respuesta no exitosa: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Game>, t: Throwable) {
                                    Log.e("LOAD_GAMES", "Error en la llamada a la API", t)
                                }
                            })
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
                recyclerViewFriends.adapter = adapterFriends
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar amistades", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}
