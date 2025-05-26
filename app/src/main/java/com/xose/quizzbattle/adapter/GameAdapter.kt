import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameAdapter(
    private val partidas: List<Game>,
    private val usuarioLogueado: User,
    private val onJugarClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.PartidaViewHolder>() {

    inner class PartidaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtNombre: TextView = view.findViewById(R.id.txtJugador)
        val txtResultado: TextView = view.findViewById(R.id.txtResultado)
        val btnJugar: Button = view.findViewById(R.id.btnJugar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return PartidaViewHolder(view)
    }

    override fun getItemCount(): Int = partidas.size

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {

        val partida = partidas[position]

        val esTurnoDelUsuario = partida.turn?.username == usuarioLogueado.username

        val oponente = if (partida.player1?.username == usuarioLogueado.username) partida.player2 else partida.player1
        val puntosUsuario = if (partida.player1?.username == usuarioLogueado.username) partida.starsPlayer1 else partida.starsPlayer2
        val puntosOponente = if (partida.player1?.username == usuarioLogueado.username) partida.starsPlayer2 else partida.starsPlayer1

        val context = holder.itemView.context
        val gameService = ApiClient.getGameService(context)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val profileImage = gameService.getProfileImage(oponente.username)

                if (profileImage.imageBase64 != null || !profileImage.imageBase64.isEmpty()) {
                    val base64Image =
                        profileImage.imageBase64.substringAfter("base64,", profileImage.imageBase64)
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    holder.imgAvatar.setImageBitmap(bitmap)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Base64", "Error al convertir la imagen Base64: ${e.message}")
            }
        }

        holder.txtNombre.text = oponente?.username
        holder.txtResultado.text = "$puntosUsuario-$puntosOponente"
        holder.btnJugar.visibility = if (esTurnoDelUsuario && partida.status == Game.Status.ONGOING) View.VISIBLE else View.GONE

        holder.btnJugar.setOnClickListener {
            onJugarClick(partida)
        }
    }

}
