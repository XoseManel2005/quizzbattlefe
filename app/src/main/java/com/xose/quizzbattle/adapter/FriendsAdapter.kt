import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xose.quizzbattle.R
import com.xose.quizzbattle.data.ApiClient
import com.xose.quizzbattle.model.Friendship
import com.xose.quizzbattle.model.Game
import com.xose.quizzbattle.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FriendsAdapter(
    private val friends: List<Friendship>,
    private val usuarioLogueado: User,
    private val onJugarClick: (User) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtNombre: TextView = view.findViewById(R.id.txtJugador)
        val btnJugar: Button = view.findViewById(R.id.btnJugar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return FriendsViewHolder(view)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friends[position]
        val user : String
        if (usuarioLogueado.username == friend.sender.username){
            holder.txtNombre.text = friend.receiver.username
            user = friend.receiver.username
            holder.btnJugar.setOnClickListener {
                onJugarClick(friend.receiver)
            }
        } else {
            holder.txtNombre.text = friend.sender.username
            user = friend.sender.username
            holder.btnJugar.setOnClickListener {
                onJugarClick(friend.sender)
            }
        }
        val context = holder.itemView.context
        val gameService = ApiClient.getGameService(context)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val profileImage = gameService.getProfileImage(user)

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
    }

}
