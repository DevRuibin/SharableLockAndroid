package com.example.shareablelock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity() {
    private val TAG = "MessageActivity"
    lateinit var messageList: List<MessageModel>
    lateinit var adapter: MessageAdapter
    lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_locks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_locks -> {
                    val intent = Intent(this, Locks::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_notifications -> {
                    finish()
                }
                R.id.nav_key -> {
                    val intent = Intent(this, KeyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        apiService = RetrofitHelper.getApiService(this)
        recyclerView = findViewById(R.id.recyclerView)
        getAllMessages()
    }

    private fun getAllMessages() {
        Log.d(TAG, "getAllMessages: called, getting messages by user id")
        val userId = PreferenceHelper.getUser(this)?.id
        Log.d(TAG, "getAllMessages: userId: $userId")
        userId?.let {
            apiService.getMessagesByUser(it).enqueue(object : Callback<List<MessageModel>> {
                override fun onResponse(call: Call<List<MessageModel>>, response: Response<List<MessageModel>>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "onResponse: ${response.body()}")
                        Log.d(TAG, "onResponse: ${response.body()?.size}")
                        messageList = response.body()!!
                        adapter = MessageAdapter(this@MessageActivity, 1)
                        adapter.messages = messageList
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this@MessageActivity)
                    }else{
                        Log.d(TAG, "onFailResponse: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<List<MessageModel>>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })}?:run{
            Log.d(TAG, "getAllMessages: userId is null")
        }
    }
}