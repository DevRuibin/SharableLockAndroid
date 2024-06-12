package com.example.shareablelock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Locks : AppCompatActivity() {
    private val TAG = "Locks"

    lateinit var lockList: List<LockModel>
    lateinit var adapter: LockAdapter
    lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_locks)
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
                    // do nothing
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        apiService = RetrofitHelper.getApiService(this)
        recyclerView = findViewById(R.id.recycleView)
        getAllLocks()

    }

    private fun getAllLocks(){
        Log.d(TAG, "getAllLocks: called")
        val userId = PreferenceHelper.getUser(this)?.id
        userId?.let {
            apiService.getLocksByUser(it).enqueue(object : Callback<List<LockModel>> {
                override fun onResponse(call: Call<List<LockModel>>, response: Response<List<LockModel>>) {
                    if(response.isSuccessful){
                        lockList = response.body()!!
                        Log.d(TAG, "onResponse: ${lockList.size}")
                        Log.d(TAG, "onResponse: $lockList")
                        adapter = LockAdapter(this@Locks)
                        adapter.locks = lockList
                        recyclerView.adapter = adapter
                        val gridLayoutManager = GridLayoutManager(this@Locks, 2)
                        recyclerView.layoutManager = gridLayoutManager
                    }
                }

                override fun onFailure(call: Call<List<LockModel>>, t: Throwable) {
                    Toast.makeText(this@Locks, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }?:run{
            finish()
        }
    }


}