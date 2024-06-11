package com.example.shareablelock

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/v1/users")
    fun createUser(@Body user: UserRequest): Call<UserModel>

    @POST("/api/v1/users/login")
    fun loginUser(@Body user: UserRequest): Call<UserModel>

}