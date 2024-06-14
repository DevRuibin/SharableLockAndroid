package com.example.shareablelock

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder
import java.text.DateFormat
import java.util.Date
import java.util.Locale


class MessageAdapter(val context: Context): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val TAG = "MessageAdapter"

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        var txtUsername: TextView = itemView.findViewById(R.id.txtUsername)
        var txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        var txtTime: TextView = itemView.findViewById(R.id.txtTime)
        var cardNewMessage: MaterialCardView = itemView.findViewById(R.id.cardNewMessage)
        var txtNewMessageNum: TextView = itemView.findViewById(R.id.txtNewMessageNum)
        var id: Long = -1
        var messageUserResponse: MessageUserResponse? = null

        init {
            itemView.setOnClickListener {
                Log.d(TAG, "MessageViewHolder: Clicked")
                Log.d(TAG, "MessageViewHolder: $id")
                val intent = Intent(context, MessageDetailActivity::class.java)
                intent.putExtra("userId", id)
                Log.d(TAG, "MessageViewHolder: $messageUserResponse")
                intent.putExtra("messageUserResponse", messageUserResponse)
                context.startActivity(intent)
            }

        }
    }

        private var _messages: List<MessageUserResponse> = ArrayList()
        var messages: List<MessageUserResponse>
            get() = _messages
            set(value) {
                _messages = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MessageAdapter.MessageViewHolder {
            val view: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
            Log.d(TAG, "OnBindViewHolder: Called")
            holder.messageUserResponse = messages[position]
            val senderId = messages[position].messages[0].senderId
            val message = messages[position].messages[0].text
            val date = Date(messages[position].messages[0].timestamp)
            val unreadNum = messages[position].unreadMessageNum
            val format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.FRANCE)
            val formattedDate = format.format(date)
            holder.txtMessage.text = message
            holder.txtTime.text = formattedDate
            holder.txtNewMessageNum.text = unreadNum.toString()
            if(unreadNum == 0){
                holder.cardNewMessage.visibility = View.GONE
            }
            if(senderId == 0L){
                holder.txtUsername.text = "System"
                val url = StringBuilder(context.getString(R.string.file_server_ip)).append("/").append("a.jpg").toString()

                Glide.with(holder.itemView.context)
                    .asBitmap()
                    .load(url)
                    .into(holder.imgProfile)
                return
            }
            val apiService = RetrofitHelper.getApiService(context)
            apiService.getUser(senderId).enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        holder.txtUsername.text = user?.username
                        Glide.with(holder.itemView.context)
                            .asBitmap()
                            .load(user?.avatar)
                            .into(holder.imgProfile)
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })

        }

        override fun getItemCount(): Int = messages.size

}
