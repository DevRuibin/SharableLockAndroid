package com.example.shareablelock

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserModel (
    val id: Long? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var avatar: String? = null,
    var gender: Gender? = null,
    var admin:Boolean = false
) : Parcelable

