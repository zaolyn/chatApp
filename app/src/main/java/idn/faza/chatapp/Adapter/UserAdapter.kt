package idn.faza.chatapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import idn.faza.chatapp.Activity.MainActivity
import idn.faza.chatapp.Activity.MessageChat
import idn.faza.chatapp.R
import idn.faza.chatapp.model.Users
import kotlinx.android.synthetic.main.activity_main.*

class UserAdapter(
    private val mContext: Context,
    private val mUsers: List<Users>,
    private var isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.user_search_iterm_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.e("ya", mUsers.size.toString())
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user= mUsers[i]
        Log.e("hah", user.toString())
        holder.userNameTxt.text = user.username
        if (user.profile.isNotBlank()) Picasso.get().load(user.profile)
            .placeholder(R.drawable.profile).into(holder.profileImageView)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "visit Profile"
            )

            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("what do yo want")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0)
                {
                    val intent = Intent(mContext, MessageChat::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }

                if (position == 1)
                {


                }
            })
            builder.show()

        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.profile_image)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        var offlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)

    }
}
