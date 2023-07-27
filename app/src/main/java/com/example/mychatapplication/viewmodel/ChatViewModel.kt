package com.example.mychatapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mychatapplication.model.repositary.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val Repo: NetworkRepository) : ViewModel() {

}