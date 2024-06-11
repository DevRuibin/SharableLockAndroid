package com.example.shareablelock


class UserModel {
    private val id: Long? = null
    private val username: String? = null
    private val email: String? = null
    private val phone: String? = null
    private val avatar: String? = null
    private val gender: Gender? = null
    private val admin = false

    override fun toString(): String {
        return "UserModel(id=$id, username=$username, " +
                "email=$email, phone=$phone, avatar=$avatar,"+
                "gender=$gender, admin=$admin)"
    }
}

