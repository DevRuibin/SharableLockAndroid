package com.example.shareablelock

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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


    @Multipart
    @POST("/api/v1/files/upload")
    fun uploadFile(@Part file: MultipartBody.Part)
    : Call<ProfilePhotoName>

    /*Get all locks by user id*/
    @GET("/api/v1/locks/user/{user-id}")
    fun getLocksByUser(@Path("user-id") userId: Long): Call<List<LockModel>>

    /*Get a lock by lock id*/
    @GET("/api/v1/locks/{lock-id}")
    fun getLockById(@Path("lock-id") lockId: Long): Call<LockModel>

    /*Create a lock*/
    @POST("/api/v1/locks")
    fun createLock(@Body lock: LockModel): Call<LockModel>

    /*Update a lock*/
    @PATCH("/api/v1/locks/{lock-id}")
    fun updateLock(@Path("lock-id") lockId: Long, @Body lock: LockModel): Call<LockModel>

    /*Get all Messages by user id*/
    @GET("/api/v1/messages/users/{user-id}")
    fun getMessagesByUser(@Path("user-id") userId: Long): Call<List<MessageModel>>
}