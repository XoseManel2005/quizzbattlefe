import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FinishedGameAdapter(
    private val partidas: List<Game>,
    private val usuarioLogueado: User
) : RecyclerView.Adapter<FinishedGameAdapter.PartidaViewHolder>() {

    inner class PartidaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtNombre: TextView = view.findViewById(R.id.txtJugador)
        val imgAvatar2: ImageView = view.findViewById(R.id.imgAvatar2)
        val txtNombre2: TextView = view.findViewById(R.id.txtJugador2)
        val txtResultado: TextView = view.findViewById(R.id.txtResultado)
        val llFinishedGame: LinearLayout = view.findViewById(R.id.llFinishedGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finished_game_item, parent, false)
        return PartidaViewHolder(view)
    }

    override fun getItemCount(): Int = partidas.size

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        val partida = partidas[position]
        holder.txtNombre.text = partida.player1?.username
        holder.txtNombre2.text = partida.player2?.username
        holder.txtResultado.text = "${partida.starsPlayer1}-${partida.starsPlayer2}"
        val isPlayer1 = partida.player1?.username == usuarioLogueado.username
        val playerStars = if (isPlayer1) partida.starsPlayer1 else partida.starsPlayer2
        val opponentStars = if (isPlayer1) partida.starsPlayer2 else partida.starsPlayer1

        val context = holder.itemView.context
        val gameService = ApiClient.getGameService(context)

        GlobalScope.launch(Dispatchers.Main) {
            try {//PLayer1
                val profileImage1 = gameService.getProfileImage(partida.player1.username)

                if (profileImage1.imageBase64 != null || !profileImage1.imageBase64.isEmpty()) {
                    val base64Image =
                        profileImage1.imageBase64.substringAfter("base64,", profileImage1.imageBase64)
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    holder.imgAvatar.setImageBitmap(bitmap)
                }

                //PLayer1
                val profileImage2 = gameService.getProfileImage(partida.player2.username)
                if (profileImage2.imageBase64 != null || !profileImage2.imageBase64.isEmpty()) {
                    val base64Image = profileImage2.imageBase64.substringAfter(
                        "base64,",
                        profileImage2.imageBase64
                    )
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    holder.imgAvatar2.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Base64", "Error al convertir la imagen Base64: ${e.message}")
            }
        }
        val backgroundRes = when {
            playerStars != null && opponentStars != null -> when {
                playerStars > opponentStars -> R.drawable.bg_win
                playerStars < opponentStars -> R.drawable.bg_lose
                else -> R.drawable.bg_draw
            }
            else -> android.R.color.transparent
        }
        holder.llFinishedGame.setBackgroundResource(backgroundRes)


    }

}
