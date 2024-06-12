package com.example.shareablelock


import kotlin.collections.ArrayList


data class LockModel(
    var id: Long? = null,
    var name: String? = null,
    var picture: String? = null,
    var uid: String? = null,
    var power: Float = 0.0f,
    var online: Boolean = false,
    var locked: Boolean = false,
    var ownerId: Long? = null,
    var latitude: Float = 0.0f,
    var longitude: Float = 0.0f,
    var reportBattery: Int = 0,
    var reportLocation: Int = 0,
    var users: List<Long> = ArrayList(),
    var managers: List<Long> = ArrayList()
)

