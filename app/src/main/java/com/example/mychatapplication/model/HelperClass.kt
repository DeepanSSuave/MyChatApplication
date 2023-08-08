package com.example.mychatapplication.model

import retrofit2.Call

data class TokenRequestBody(
    var username : String ,
    var password: String
)

data class Tokens(
    var access : String ,
    var refresh : String
)

data class VerifyResponse(
    var detail : String,
    var code : String
)

data class LogoutResponse(
    var message : String
)

data class LogoutRequest(
    var token : String
)

data class VerifyRequest(
    var token : String
)

data class GetAccessTokenRequest(
    var refresh: String
)

data class GetAccessTokenResponse(
    var access: String
)