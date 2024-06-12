package com.example.shareablelock

import java.io.Serializable



data class MessageModel(val id: Long,
                        val type: MessageType,
                        val senderId: Long,
                        val toUserId: Long,
                        val text: String,
                        val detail: String,
                        val timestamp: Long,
                        val read: Boolean)

enum class MessageType {
    GENERAL,
    NOTIFICATION,
    STATUS
}