package com.example.mychatapplication

import com.example.mychatapplication.model.*
import com.example.mychatapplication.model.repositary.CurrentUser
import com.example.mychatapplication.model.repositary.GetMessageObject
import com.example.mychatapplication.model.repositary.GetMessageObjectItem
import com.example.mychatapplication.model.repositary.Message
import dagger.Module
import retrofit2.Call
import retrofit2.http.*


interface Network {

    @POST("api/token/")
    fun getTokens(
        @Body userDetails: TokenRequestBody?
    ): Call<Tokens>?

    @POST("api/token/refresh/")
    fun getAccessToken(
        @Body refreshToken: GetAccessTokenRequest?
    ): Call<GetAccessTokenResponse>?

    @POST("api/token/verify/")
    fun verifyToken(
        @Body token: VerifyRequest?
    ): Call<VerifyResponse>?

    @POST("api/token/verify/")
    fun logoutUser(
        @Body token: LogoutRequest?
    ): Call<LogoutResponse>?

    @Headers("Content-Type: application/json")
    @GET("get_user/")
    fun getCurrentUser(
    ): Call<CurrentUser>?

    @Headers("Content-Type: application/json")
    @GET("get_groups/")
    fun getGroups(
    ):Call<List<GetMessageObjectItem>>?

    @Headers("Content-Type: application/json")
    @GET("get_messages/{id}")
    fun getMessages(
        @Path("id") id: Int
    ):Call<List<Message>>?
}