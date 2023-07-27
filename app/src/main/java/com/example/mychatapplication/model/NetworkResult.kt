package com.example.mychatapplication.model

sealed class NetworkResult<out T>
object OnNetworkLoading : NetworkResult<Nothing>()
data class OnNetworkErrorRes(val messageRes: Int) : NetworkResult<Nothing>()
data class OnNetworkError(val message: String) : NetworkResult<Nothing>()
data class OnNetworkSuccess<T>(val payload: T) : NetworkResult<T>()