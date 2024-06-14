package com.example.shareablelock

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MessageUserResponse(
    var user: UserModel,
    var unreadMessageNum: Int,
    var messages: List<MessageModel>
): Parcelable

@Parcelize
data class MessageModel(val id: Long,
                        val type: MessageType,
                        val senderId: Long,
                        val toUserId: Long,
                        val text: String,
                        val detail: String,
                        val timestamp: Long,
                        var read: Boolean): Parcelable

enum class MessageType {
    GENERAL,
    NOTIFICATION,
    STATUS
}