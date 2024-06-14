package com.example.shareablelock

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Locale

class MessageDetailAdapter(val context: Context, val userId: Long): RecyclerView.Adapter<MessageDetailAdapter.MessageDetailViewHolder>(){
    private val TAG = "MessageDetailAdapter"

    inner class MessageDetailViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var txtInMessage: TextView = itemView.findViewById(R.id.txtInMessage)
        var txtOutMessage: TextView = itemView.findViewById(R.id.txtOutMessage)
        var txtInTime: TextView = itemView.findViewById(R.id.txtInTime)
        var txtOutTime: TextView = itemView.findViewById(R.id.txtOutTime)

        init {
            itemView.setOnClickListener {
                // Handle the click event
            }

            txtInMessage.setOnClickListener {
                // Handle the click event
            }

            txtOutMessage.setOnClickListener {
                // Handle the click event
            }

        }
    }

    private var _messages: List<MessageModel> = ArrayList()
    var messages: List<MessageModel>
        get() = _messages
        set(value) {
            _messages = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageDetailAdapter.MessageDetailViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_only_message_time, parent, false)
        return MessageDetailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessageDetailAdapter.MessageDetailViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder: $position")
        val message = messages[position]
        val format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.FRANCE)
        val formattedDate = format.format(message.timestamp)
        if(message.senderId != userId){
            holder.txtOutMessage.text = message.text
            holder.txtInTime.text = formattedDate

            holder.txtInMessage.visibility = View.GONE
            holder.txtInTime.visibility = View.GONE
        } else {


            holder.txtInMessage.text = message.text
            holder.txtOutTime.text = formattedDate
            holder.txtOutMessage.visibility = View.GONE
            holder.txtOutTime.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

}