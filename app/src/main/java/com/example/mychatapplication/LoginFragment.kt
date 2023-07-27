package com.example.mychatapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.mychatapplication.databinding.FragmentLoginBinding
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.OnNetworkError
import com.example.mychatapplication.model.OnNetworkErrorRes
import com.example.mychatapplication.model.OnNetworkLoading
import com.example.mychatapplication.model.OnNetworkSuccess
import com.example.mychatapplication.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var preference: ChatPreferenceManager

    private val mUserViewModel: UserViewModel by viewModels()

    lateinit var binding : FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(requireContext(), SocketService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)

        if (preference.getString("AccessToken").isNotEmpty() && preference.getString("AccessToken") != null){
            getVerifiedToken(preference.getString("AccessToken"))
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener{
            val username = binding.textUsername.text.toString()
            val password = binding.textPwd.text.toString()
            getTokens(username,password)
            getCurrentUser()
        }
    }

    private fun getCurrentUser() {
        mUserViewModel.getCurrentUser().observe(this){
            when (it) {
                OnNetworkLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is OnNetworkError -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", it.message)
                }
                is OnNetworkErrorRes -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", it.messageRes.toString())
                }
                is OnNetworkSuccess -> {
                    Log.e("CurrentUser", it.payload.toString())
                    preference.setCurrentUser(it.payload.toString())
                    it.payload?.id?.let { id1 -> preference.set("UserID", id1) }
                    it.payload?.username?.let { userName -> preference.set("UserName", userName) }
                }
            }
        }
    }

    private fun getTokens(username: String, password: String) {
        mUserViewModel.getTokens(username, password).observe(this) {
            when (it) {
                OnNetworkLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is OnNetworkError -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", it.message)
                }
                is OnNetworkErrorRes -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", it.messageRes.toString())
                }
                is OnNetworkSuccess -> {
                    Log.e("Tokens", it.payload.toString())
                    it.payload?.access?.let { accessToken -> preference.set("AccessToken", accessToken) }
                    it.payload?.refresh?.let { refreshToken -> preference.set("RefreshToken", refreshToken) }
//                    val serviceIntent = Intent(this, WebSocketService::class.java)
//                    context?.let { it1 -> ContextCompat.startForegroundService(it1,serviceIntent) }
                    this.findNavController().navigate(R.id.DashboardFragment)
                }
            }
        }
    }

    private fun getVerifiedToken(token : String) {
        mUserViewModel.getTokenVerified(token).observe(this){
            when (it) {
                OnNetworkLoading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is OnNetworkError -> {
                    if (it.message == "401") {
                        mUserViewModel.getAccessToken(preference.getString("RefreshToken")).observe(this) {
                            when(it) {
                                is OnNetworkLoading -> {

                                }
                                is OnNetworkError -> {

                                }
                                is OnNetworkErrorRes -> {

                                }
                                is OnNetworkSuccess -> {
                                    it.payload?.access?.let { preference.set("AccessToken", it) }
                                }
                            }
                        }
                    }
//                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", it.message)
                }
                is OnNetworkErrorRes -> {
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),it.messageRes.toString(),Toast.LENGTH_SHORT).show()
                    Log.d("Error", it.messageRes.toString())
                }
                is OnNetworkSuccess -> {
                    Log.e("CurrentUser", it.payload.toString())
                    this.findNavController().navigate(R.id.DashboardFragment)
                }
            }
        }
    }

}