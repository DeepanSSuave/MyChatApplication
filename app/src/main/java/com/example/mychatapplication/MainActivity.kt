package com.example.mychatapplication

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mychatapplication.databinding.ActivityMainBinding
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.OnNetworkError
import com.example.mychatapplication.model.OnNetworkErrorRes
import com.example.mychatapplication.model.OnNetworkLoading
import com.example.mychatapplication.model.OnNetworkSuccess
import com.example.mychatapplication.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val username = "User2"
//        val password = "12345678Aa"
//        val username = "User3"
//        val password = "12345678Aa"

    }
}