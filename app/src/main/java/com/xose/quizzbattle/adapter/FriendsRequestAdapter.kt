import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.model.Friendship
import com.xose.quizzbattle.model.User

class FriendsRequestAdapter(
    private val friends: List<Friendship>,
    private val usuarioLogueado: User,
    private val onAcceptClick: (Friendship) -> Unit,
    private val onDenyClick: (Friendship) -> Unit
) : RecyclerView.Adapter<FriendsRequestAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtNombre: TextView = view.findViewById(R.id.txtJugador)
        val btnAccept: ImageView = view.findViewById(R.id.btnAccept)
        val btnDeny: ImageView = view.findViewById(R.id.btnDeny)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_item, parent, false)
        return FriendsViewHolder(view)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        if (friends.isNullOrEmpty()){

        }
        val friend = friends[position]
        if (usuarioLogueado.username == friend.sender.username){
            holder.txtNombre.text = friend.receiver.username
            holder.btnAccept.setOnClickListener {
                onAcceptClick(friend)
            }
            holder.btnDeny.setOnClickListener {
                onDenyClick(friend)
            }
        } else {
            holder.txtNombre.text = friend.sender.username
            holder.btnAccept.setOnClickListener {
                onAcceptClick(friend)
            }
            holder.btnDeny.setOnClickListener {
                onDenyClick(friend)
            }
        }
    }

}
