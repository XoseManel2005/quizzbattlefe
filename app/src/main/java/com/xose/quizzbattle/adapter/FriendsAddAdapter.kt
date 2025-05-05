import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.model.Friendship
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User

class FriendsAddAdapter(
    private val players: List<User>,
    private val onAddClick: (User) -> Unit
) : RecyclerView.Adapter<FriendsAddAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtNombre: TextView = view.findViewById(R.id.txtJugador)
        val imgAdd: ImageView = view.findViewById(R.id.imgAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_add_item, parent, false)
        return FriendsViewHolder(view)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val player = players[position]

            holder.txtNombre.text = player.username
            holder.imgAdd.setOnClickListener {
                onAddClick(player)
            }

    }

}
