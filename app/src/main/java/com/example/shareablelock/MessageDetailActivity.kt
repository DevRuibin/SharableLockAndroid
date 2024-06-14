package com.example.shareablelock

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MessageDetailActivity : AppCompatActivity() {
    private val TAG = "MessageDetailActivity"
    private lateinit var messageUserResponse: MessageUserResponse
    private lateinit var imgUser: ImageView
    private lateinit var txtUsername: TextView
    private lateinit var adapter: MessageDetailAdapter
    lateinit var recyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tempMessageUserResponse = intent.getParcelableExtra<MessageUserResponse>("messageUserResponse")
        if (tempMessageUserResponse != null) {
            messageUserResponse = tempMessageUserResponse
            Log.d(TAG, "onCreate: $messageUserResponse")
        } else {
            Log.e(TAG, "onCreate: messageUserResponse is null")
            // Handle the error appropriately, such as finishing the activity or showing an error message
            finish()
        }
        messageUserResponse.messages.forEach {
//            it.read = true
            // todo change the message status to read
        }

        imgUser = findViewById(R.id.imgUser)
        txtUsername = findViewById(R.id.txtUserName)

        Glide.with(this)
            .load(messageUserResponse.user.avatar)
            .into(imgUser)
        txtUsername.text = messageUserResponse.user.username


        adapter = MessageDetailAdapter(this, messageUserResponse.user.id!!)
        adapter.messages = messageUserResponse.messages
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}