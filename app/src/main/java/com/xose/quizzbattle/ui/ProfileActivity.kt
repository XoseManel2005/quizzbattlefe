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

import java.io.ByteArrayOutputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var usuarioLogueado: User

    // ImageViews
    private lateinit var imgProfilePic: ImageView
    private lateinit var imgProfileBar: ImageView
    private lateinit var imgEditUsername: ImageView
    private lateinit var imgGames: ImageView
    private lateinit var imgFriendship: ImageView

    // TextViews
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvLogout: TextView
    private lateinit var tvChangePassword: TextView
    private lateinit var tvFriends: TextView
    private lateinit var tvWonGames: TextView
    private lateinit var tvTotalGames: TextView

    // EditTexts
    private lateinit var etUsername: EditText

    // Buttons
    private lateinit var btnSaveUsername: Button
    private lateinit var btnAddFriend: Button

    // Media picker
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usuarioLogueado = SessionManager(this).getLoggedUser() as User

        imgProfilePic = findViewById(R.id.imgProfilePic)
        imgProfileBar = findViewById(R.id.imgProfile)
        imgEditUsername = findViewById(R.id.imgEditUsername)
        imgGames = findViewById(R.id.imgGames)
        imgFriendship = findViewById(R.id.imgFriendships)

        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvLogout = findViewById(R.id.tvLogout)
        tvChangePassword = findViewById(R.id.tvChangePassword)
        tvFriends = findViewById(R.id.tvFriends)
        tvWonGames = findViewById(R.id.tvWonGames)
        tvTotalGames = findViewById(R.id.tvTotalGames)

        etUsername = findViewById(R.id.etUsername)

        btnSaveUsername = findViewById(R.id.btnSaveUsername)
        btnAddFriend = findViewById(R.id.btnAddFriend)



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

        loadProfileImage()
        loadProfileStats()

        val underlinePasswordText = SpannableString(tvChangePassword.text)
        underlinePasswordText.setSpan(UnderlineSpan(), 0, underlinePasswordText.length, 0)
        tvChangePassword.text = underlinePasswordText

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
            val intent = Intent (this, FriendshipsActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgGames.setOnClickListener {
            val intent = Intent (this, GamesActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgFriendship.setOnClickListener {
            val intent = Intent (this, FriendshipsActivity::class.java)
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

    private fun loadProfileImage() {
        lifecycleScope.launch {
            try {
                val gameService = ApiClient.getGameService(this@ProfileActivity)
                val profileImage = gameService.getProfileImage(usuarioLogueado.username)

                if (!profileImage.imageBase64.isNullOrEmpty()) {
                    try {
                        val base64Image = profileImage.imageBase64.substringAfter("base64,", profileImage.imageBase64)
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imgProfilePic.setImageBitmap(bitmap)
                        imgProfileBar.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Base64", "Error al convertir la imagen Base64: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API", "Error al cargar la imagen de perfil: ${e.message}")
            }
        }
    }

    private fun loadProfileStats() {
        lifecycleScope.launch {
            try {
                val gameService = ApiClient.getGameService(this@ProfileActivity)

                val friendships = gameService.getAcceptedFriendships(usuarioLogueado.username)
                tvFriends.text = friendships.size.toString()

                val finishedGames = gameService.getGames(usuarioLogueado.username, "FINISHED")

                val wonGames = finishedGames.count { game ->
                    game.winner?.id == usuarioLogueado.id
                }
                tvWonGames.text = wonGames.toString()

                val ongoingGames = gameService.getGames(usuarioLogueado.username, "ONGOING")
                val allGames = finishedGames + ongoingGames

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
    }

}