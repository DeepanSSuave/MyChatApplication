package com.example.mychatapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapplication.model.*
import com.example.mychatapplication.model.repositary.GetMessageObject
import com.example.mychatapplication.model.repositary.GetMessageObjectItem
import com.example.mychatapplication.model.repositary.Message
import com.example.mychatapplication.model.repositary.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val Repo: NetworkRepository) : ViewModel() {

    fun getGroups(): MutableLiveData<NetworkResult<List<GetMessageObjectItem>?>> {
        val result = MutableLiveData<NetworkResult<List<GetMessageObjectItem>?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData = Repo.getGroups()
                resultData?.enqueue(object : Callback<List<GetMessageObjectItem>?> {

                    override fun onFailure(call: Call<List<GetMessageObjectItem>?>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<List<GetMessageObjectItem>?>,
                        response: Response<List<GetMessageObjectItem>?>
                    ) {
                        if (response.isSuccessful) {
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        }
                    }
                })
            } catch (e: Throwable) {
                result.postValue(OnNetworkError("Error Occurred while getting groups"))
            }
        }
        return result
    }

    fun getMessages(id : Int): MutableLiveData<NetworkResult<List<Message>?>> {
        val result = MutableLiveData<NetworkResult<List<Message>?>>()
        result.value = OnNetworkLoading
        viewModelScope.launch {
            try {
                val resultData = Repo.getMessages(id)
                resultData?.enqueue(object : Callback<List<Message>?> {

                    override fun onFailure(call: Call<List<Message>?>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<List<Message>?>,
                        response: Response<List<Message>?>
                    ) {
                        if (response.isSuccessful) {
                            result.postValue(OnNetworkSuccess(payload = response.body()))
                        }
                    }
                })
            } catch (e: Throwable) {
                result.postValue(OnNetworkError("Error Occurred while getting Messages"))
            }
        }
        return result
    }
}