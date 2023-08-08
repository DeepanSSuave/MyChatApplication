package com.example.mychatapplication.model.repositary

import com.example.mychatapplication.Network
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.*
import retrofit2.Call
import javax.inject.Inject


class NetworkRepository @Inject constructor(
    private var networkApi: Network,
    private var preference: ChatPreferenceManager
) {

    fun getAccessAndRefreshToken(username : String, pwd : String) : Call<Tokens>? {
        try {
            return networkApi.getTokens(TokenRequestBody(username,pwd))
        } catch (error : Throwable) {
            throw error
        }
    }

    fun getCurrentUser() : Call<CurrentUser>? {
        try {
            return networkApi.getCurrentUser()
        }catch (error : Throwable) {
            throw error
        }
    }

    fun getGroups() : Call<List<GetMessageObjectItem>>? {
        try {
            return networkApi.getGroups()
        }catch (e : Throwable) {
            throw e
        }
    }

    fun getMessages(id : Int) : Call<List<Message>>? {
        try {
            return networkApi.getMessages(id)
        }catch (e : Throwable) {
            throw e
        }
    }

    fun getTokenVerified(token : String) : Call<VerifyResponse>? {
        try {
            return networkApi.verifyToken(VerifyRequest(token))
        }catch (e : Throwable) {
            throw e
        }
    }

    fun getUserLoggedOut(token : String) : Call<LogoutResponse>? {
        try {
            return networkApi.logoutUser(LogoutRequest(token))
        }catch (e : Throwable) {
            throw e
        }
    }

    fun getAccessToken(token : String) : Call<GetAccessTokenResponse>? {
        try {
            return networkApi.getAccessToken(GetAccessTokenRequest(token))
        }catch (e : Throwable) {
            throw e
        }
    }
}