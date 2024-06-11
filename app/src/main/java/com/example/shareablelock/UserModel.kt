package com.example.shareablelock


data class UserModel (
    val id: Long? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var avatar: String? = null,
    var gender: Gender? = null,
    var admin:Boolean = false
)

