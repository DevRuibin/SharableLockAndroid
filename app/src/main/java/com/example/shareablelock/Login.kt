package com.example.shareablelock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiService = RetrofitHelper.getApiService(this)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPwd = findViewById<EditText>(R.id.editPwd)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)

        btnSignIn.setOnClickListener {
            val email = editEmail.text.toString()
            val pwd = editPwd.text.toString()
            val userRequest = UserRequest(email, pwd)
            apiService.loginUser(userRequest).enqueue(object : Callback<UserModel>{
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>){
                    if(response.isSuccessful){
                        val userModel = response.body()
                        userModel?.let{
                            PreferenceHelper.saveUser(this@Login, it)
                            val intent = Intent(this@Login, Locks::class.java)
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(this@Login, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable){
                    Toast.makeText(this@Login, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
}