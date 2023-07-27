package com.example.mychatapplication.model.repositary

data class GetMessageObjectItem(
    var created_date: String?,
    var created_user: CreatedUser?,
    var id: Int?,
    var last_message: String?,
    var members: List<Member?>?,
    var name: String?
)