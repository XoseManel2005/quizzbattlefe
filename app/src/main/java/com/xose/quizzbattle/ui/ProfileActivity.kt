package com.xose.quizzbattle.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.ImageRequest
import com.xose.quizzbattle.model.User
import com.xose.quizzbattle.util.SessionManager
import kotlinx.coroutines.launch
import android.util.Log
import com.google.gson.Gson
import com.xose.quizzbattle.model.Friendship
import org.w3c.dom.Text

import java.io.ByteArrayOutputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var usuarioLogueado: User
    private lateinit var imgProfilePic: ImageView
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usuarioLogueado = SessionManager(this).getLoggedUser() as User
        imgProfilePic = findViewById(R.id.imgProfilePic)

        // Registrar el callback aquí, donde ya imgProfilePic está inicializado
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val gameService = ApiClient.getGameService(this@ProfileActivity)
                imgProfilePic.setImageURI(uri)

                // Convertir URI a Base64
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val imageBytes = outputStream.toByteArray()
                    val imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    val imageRequest = ImageRequest(
                        username = usuarioLogueado.username,
                        imageBase64 = imageBase64
                    )

                    val call: Call<String> = gameService.uploadProfileImage(imageRequest)
                    call.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                Log.d("API", "Imagen subida: ${response.body()}")
                            } else {
                                Log.e("API", "Error al subir la imagen: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.e("API", "Fallo en la llamada: ${t.message}")
                        }
                    })
                }
            } else {
                // Imagen no seleccionada
                Log.d("Base64", "Imagen no seleccionada")
            }
        }

        imgProfilePic.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        val tvLogout = findViewById<TextView>(R.id.tvLogout)
        val btnAddFriend = findViewById<Button>(R.id.btnAddFriend)
        val imgGames = findViewById<ImageView>(R.id.imgGames)
        val imgFriendship = findViewById<ImageView>(R.id.imgFriendships)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val imgEditUsername = findViewById<ImageView>(R.id.imgEditUsername)
        val btnSaveUsername = findViewById<Button>(R.id.btnSaveUsername)
        val tvChangePassword = findViewById<TextView>(R.id.tvChangePassword)

        tvUsername.text = usuarioLogueado.username
        etUsername.setText(usuarioLogueado.username)

        tvEmail.setText(usuarioLogueado.email)

        tvChangePassword.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

            val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
            val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Cambiar contraseña")
            builder.setView(dialogView)
            builder.setPositiveButton("Cambiar") { dialog, _ ->
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()

                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    showError("Por favor, completa ambos campos.")
                    return@setPositiveButton
                }

                if (newPass != confirmPass) {
                    showError("Las contraseñas no coinciden.")
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val gameService = ApiClient.getGameService(this@ProfileActivity)

                        usuarioLogueado.password = newPass
                        val response = gameService.updateUser(usuarioLogueado)

                        if (response.isSuccessful) {
                            showMessage("Contraseña actualizada correctamente.")
                            SessionManager(this@ProfileActivity).saveLoggedUser(usuarioLogueado)
                        } else {
                            showError("Error al actualizar la contraseña.")
                        }
                    } catch (e: Exception) {
                        showError("Error: ${e.message}")
                    }
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }


        // Al tocar el icono, ocultar TextView y mostrar EditText + botón
        imgEditUsername.setOnClickListener {
            tvUsername.visibility = View.GONE
            etUsername.visibility = View.VISIBLE
            imgEditUsername.visibility = View.GONE
            btnSaveUsername.visibility = View.VISIBLE
            etUsername.requestFocus()
        }

        btnSaveUsername.setOnClickListener {
            val nuevoNombre = etUsername.text.toString().trim()
            if (nuevoNombre.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val gameService = ApiClient.getGameService(this@ProfileActivity)

                        usuarioLogueado.username = nuevoNombre

                        if (usuarioLogueado.password.isNullOrEmpty()) {
                            usuarioLogueado.password = "defaultPassword"
                        }

                        if (usuarioLogueado.email.isNullOrEmpty()) {
                            usuarioLogueado.email = "placeholder@email.com"
                        }

                        if (usuarioLogueado.role == null) {
                            usuarioLogueado.role = User.Role.PLAYER
                        }

                        Log.d("UPDATE_USER", "Enviando JSON: $usuarioLogueado")

                        val response = gameService.updateUser(usuarioLogueado)

                        Log.d("UPDATE_USER", "Código de respuesta: ${response.code()}")

                        if (response.isSuccessful) {
                            Log.d("UPDATE_USER", "Actualización exitosa")
                            SessionManager(this@ProfileActivity).saveLoggedUser(usuarioLogueado)
                            tvUsername.text = nuevoNombre
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("UPDATE_USER", "Error al actualizar: $errorBody")
                        }

                        etUsername.visibility = View.GONE
                        btnSaveUsername.visibility = View.GONE
                        tvUsername.visibility = View.VISIBLE
                        imgEditUsername.visibility = View.VISIBLE

                    } catch (e: Exception) {
                        Log.e("UPDATE_USER", "Excepción: ${e.message}", e)
                    }
                }
            }
        }

        val tvFriends = findViewById<TextView>(R.id.tvFriends)

        val tvWonGames = findViewById<TextView>(R.id.tvWonGames)

        val tvTotalGames = findViewById<TextView>(R.id.tvTotalGames)

        lifecycleScope.launch {
            try {
                val gameService = ApiClient.getGameService(this@ProfileActivity)
                val profileImage = gameService.getProfileImage(usuarioLogueado.username)
                if (profileImage.imageBase64 != null || !profileImage.imageBase64.isEmpty()){
                    try {
                        // 1. Eliminar el prefijo si existe
                        val base64Image = profileImage.imageBase64.substringAfter("base64,", profileImage.imageBase64)

                        // 2. Decodificar a bytes
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

                        // 3. Convertir a Bitmap
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // 4. Asignar al ImageView
                        imgProfilePic.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Base64", "Error al convertir la imagen Base64: ${e.message}")
                    }
                }
                //recoger todas las amistades
                val friendships = gameService.getAcceptedFriendships(usuarioLogueado.username)
                tvFriends.text = friendships.size.toString()

                //obtener partidas terminadas
                val finishedGames = gameService.getGames(usuarioLogueado.username, "FINISHED")

                //ver partidas ganadas
                val wonGames = finishedGames.count { game ->
                    game.winner != null && game.winner.id == usuarioLogueado.id
                }
                tvWonGames.text = wonGames.toString()

                //obtener partidas en curso
                val ongoingGames = gameService.getGames(usuarioLogueado.username, "ONGOING")

                //sumar partidas en curso y acabadas
                val allGames = finishedGames + ongoingGames

                //las contamos y mostramos como total de partidas
                val totalGames = allGames.count { game ->
                    game.player1.id == usuarioLogueado.id || game.player2.id == usuarioLogueado.id
                }
                tvTotalGames.text = totalGames.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                tvFriends.text = "0"
                tvWonGames.text = "0"
                tvTotalGames.text = "0"
            }
        }

        val text = "Cerrar Sesión"
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, 0)

        tvLogout.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light)) // Usa tu color rojo personalizado si tienes
        tvLogout.text = spannable

        tvLogout.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAddFriend.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgGames.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgFriendship.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



    }

    private fun showError(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showMessage(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Éxito")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }

}