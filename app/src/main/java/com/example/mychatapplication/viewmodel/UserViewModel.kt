package com.example.mychatapplication.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.*
import com.example.mychatapplication.model.repositary.CurrentUser
import com.example.mychatapplication.model.repositary.NetworkRepository
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val Repo: NetworkRepository , private val pref : ChatPreferenceManager): ViewModel() {


    fun getTokens(username : String, pwd : String) : MutableLiveData<NetworkResult<Tokens?>> {
        val result = MutableLiveData<NetworkResult<Tokens?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData : Call<Tokens>? = Repo.getAccessAndRefreshToken(username,pwd)
                resultData?.enqueue(object : Callback<Tokens?> {
                    override fun onResponse(call: Call<Tokens?>, response: Response<Tokens?>) {
                        if (response.isSuccessful) {
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        }
                    }
                    override fun onFailure(call: Call<Tokens?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } catch (e : Throwable){
                result.postValue(OnNetworkError("Error Occurred while getting tokens"))
            }
        }
        return  result
    }

    fun getCurrentUser() : MutableLiveData<NetworkResult<CurrentUser?>> {
        val result  = MutableLiveData<NetworkResult<CurrentUser?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData : Call<CurrentUser>? = Repo.getCurrentUser()
                resultData?.enqueue(object : Callback<CurrentUser?> {
                    override fun onResponse(
                        call: Call<CurrentUser?>,
                        response: Response<CurrentUser?>
                    ) {
                        if (response.isSuccessful){
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        }
                    }
                    override fun onFailure(call: Call<CurrentUser?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })

            }catch (e : Throwable){
                result.postValue(OnNetworkError("Error getting current User"))
            }
        }
        return result
    }

    fun getTokenVerified(token : String) : MutableLiveData<NetworkResult<VerifyResponse?>> {
        val result  = MutableLiveData<NetworkResult<VerifyResponse?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData : Call<VerifyResponse>? = Repo.getTokenVerified(token)
                resultData?.enqueue(object : Callback<VerifyResponse?> {
                    override fun onResponse(
                        call: Call<VerifyResponse?>,
                        response: Response<VerifyResponse?>
                    ) {
                        if (response.isSuccessful){
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        } else if(response.code() == 401 && pref.getString("AccessToken").isNotEmpty()){
                            result.postValue(OnNetworkError(message = response.code().toString()))
                        }
                    }
                    override fun onFailure(call: Call<VerifyResponse?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })

            }catch (e : Throwable){
                result.postValue(OnNetworkError("Error getting verifying Token"))
            }
        }
        return result
    }

    fun getAccessToken(refreshToken : String) : MutableLiveData<NetworkResult<GetAccessTokenResponse?>> {
        val result  = MutableLiveData<NetworkResult<GetAccessTokenResponse?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData : Call<GetAccessTokenResponse>? = Repo.getAccessToken(refreshToken)
                resultData?.enqueue(object : Callback<GetAccessTokenResponse?> {
                    override fun onResponse(
                        call: Call<GetAccessTokenResponse?>,
                        response: Response<GetAccessTokenResponse?>
                    ) {
                        if (response.isSuccessful){
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        }
                    }
                    override fun onFailure(call: Call<GetAccessTokenResponse?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }catch (e : Throwable){
                result.postValue(OnNetworkError("Error getting Access Token using refreshToken"))
            }
        }
        return result
    }

}