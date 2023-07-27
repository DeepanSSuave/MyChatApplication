package com.example.mychatapplication.model.repositary

data class Message(
    var created_at: String?,
    var id: Int?,
    var isRead : Boolean = true,
    var message: String?,
    var sender: Sender?
)

