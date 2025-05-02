import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User

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

        holder.txtNombre.text = oponente?.username ?: "Desconocido"
        holder.txtResultado.text = "$puntosUsuario-$puntosOponente"
        holder.btnJugar.visibility = if (esTurnoDelUsuario && partida.status == Game.Status.ONGOING) View.VISIBLE else View.GONE

        holder.btnJugar.setOnClickListener {
            onJugarClick(partida)
        }
    }

}
