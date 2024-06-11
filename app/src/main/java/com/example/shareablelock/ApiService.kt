package com.example.shareablelock

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/api/v1/users")
    fun createUser(@Body user: UserRequest): Call<UserModel>

    @POST("/api/v1/users/login")
    fun loginUser(@Body user: UserRequest): Call<UserModel>

    @GET("/api/v1/users/{Id}")
    fun getUser(@Path("Id") id: Long): Call<UserModel>

    @GET("/api/v1/users/{user-id}/request-code")
    fun requestCode(@Path("user-id") userId: Long): Call<Unit>

    @PUT("/api/v1/users/{user-id}/change-password-with-code")
    fun updatePassword(@Path("user-id") id: Long, @Body passwordUpdateModel: PasswordUpdateModel): Call<UserModel>

    @PUT("/api/v1/users/{user-id}")
    fun updateUser(@Path("user-id") id: Long, @Body user: UserModel): Call<UserModel>

}