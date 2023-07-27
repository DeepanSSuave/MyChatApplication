package com.example.mychatapplication.model.repositary

data class ReceivingMessage(
    var sender_id: Int?,
    var created_on: String?,
    var group_id: Int?,
    var message: String?,
    var type : String?,
    var isRead : Boolean = false,
    var sender_name : String?
)