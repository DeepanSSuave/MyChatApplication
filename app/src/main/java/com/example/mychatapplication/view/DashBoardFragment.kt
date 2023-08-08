package com.example.mychatapplication.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mychatapplication.R
import com.example.mychatapplication.SocketService
import com.example.mychatapplication.`interface`.OnItemClickListener
import com.example.mychatapplication.databinding.FragmentDashBoardBinding
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.*
import com.example.mychatapplication.model.repositary.*
import com.example.mychatapplication.viewmodel.DashboardViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardFragment : Fragment() {

    @Inject
    lateinit var myPref: ChatPreferenceManager
    private val mDashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var userStoryAdapter: UserStoryAdapter
    private lateinit var groupAdapter: DashboardGroupAdapter
    private lateinit var groupname: String

    private lateinit var binding: FragmentDashBoardBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDashBoardBinding.inflate(inflater, container, false)

        userStoryAdapter = UserStoryAdapter()
        groupAdapter = DashboardGroupAdapter {
            Toast.makeText(requireContext(), "group id = ${it.id}", Toast.LENGTH_SHORT).show()
            it.id?.let { it1 -> myPref.set("GroupID", it1) }
            groupname = it.name.toString()
            it.id?.let { idGroup -> getGroupMessages(idGroup) }
        }
        binding.storyRecycler.apply {
            adapter = userStoryAdapter
        }

        binding.usersRecycler.apply {
            adapter = groupAdapter
        }

        userStoryAdapter.setStory(Constants.users)

        binding.userName.text = myPref.getString("UserName")

//        groupAdapter.setOnItemClickListener(object : OnItemClickListener {
//            override fun onItemClick(messages: List<Message?>?) {
//                val json = Gson().toJson(messages)
//
//                )
//            }
//        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDashboardViewModel.getGroups().observe(viewLifecycleOwner) {
            when (it) {
                OnNetworkLoading -> {
                }
                is OnNetworkError -> {
                    Log.d("Error", it.message)
                }
                is OnNetworkErrorRes -> {
                    Log.d("Error", it.messageRes.toString())
                }
                is OnNetworkSuccess -> {
                    Log.e("Groups", it.payload.toString())
                    groupAdapter.setData(it.payload?.let { it1 -> ArrayList(it1) })

                }
            }
        }
    }

    private fun getGroupMessages(groupId: Int) {
        mDashboardViewModel.getMessages(groupId).observe(viewLifecycleOwner) {
            when (it) {
                OnNetworkLoading -> {
                }
                is OnNetworkError -> {
                    Log.d("Error", it.message)
                }
                is OnNetworkErrorRes -> {
                    Log.d("Error", it.messageRes.toString())
                }
                is OnNetworkSuccess -> {
                    val json = Gson().toJson(it.payload)
                    Log.e("GroupMessages", json)
                    SocketService.startGroupChannel(
                        groupId,
                        myPref.getString("AccessToken"),
                        requireContext()
                    )
                    findNavController().navigate(
                        R.id.actionFromDashboardFragmentToChatFragment,
                        bundleOf("GroupMessages" to json, "GroupName" to groupname)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("new_message_action")
        requireContext().registerReceiver(messageReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(messageReceiver)
    }

    private var messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("message")
            val groupId = intent.getIntExtra("groupId", 0)
            val channelType = intent.getStringExtra("type")
            val sentOn = intent.getStringExtra("createdOn")
            val senderId = intent.getIntExtra("senderId", 0)

            if (channelType == "user_channel" && (groupId != myPref.getInt("GroupID") || myPref.getInt("GroupID") == null)) {
                Log.d("receivernewmessage", groupId.toString())

                groupAdapter.onNotificationReceive(
                    lastMessage = ReceivingMessage(
                        senderId,
                        sentOn,
                        groupId,
                        message,
                        channelType,
                        false,
                        null
                    )
                )
            }
        }
    }
}

