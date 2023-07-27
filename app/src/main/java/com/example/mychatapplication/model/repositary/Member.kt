package com.example.mychatapplication.model.repositary

data class Member(
    var email: String?,
    var first_name: String?,
    var last_name: String?,
    var username: String?
)

data class CurrentUser(
    var id: Int?,
    var first_name: String?,
    var last_name: String?,
    var username: String?,
    var email : String
)