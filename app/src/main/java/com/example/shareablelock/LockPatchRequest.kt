package com.example.shareablelock

data class LockPatchRequest(
    var eventType: EventType?,
    var name: String?= null,
    var picture: String?= null,
    var power: Float? = null,
    var online: Boolean? = null,
    var locked: Boolean? = null,
    var latitude: Float? = null,
    var longitude: Float? = null,
    var reportBattery: Int? = null,
    var reportLocation: Int? = null,
    var managerId: Long? = null,
    var userId: Long? = null
)

