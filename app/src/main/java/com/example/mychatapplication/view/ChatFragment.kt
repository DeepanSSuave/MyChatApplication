package com.example.mychatapplication.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapplication.SocketService
import com.example.mychatapplication.databinding.FragmentChatBinding
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.model.repositary.Message
import com.example.mychatapplication.model.repositary.Sender
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment(), TextWatcher {

    @Inject
    lateinit var myPreferenceManager: ChatPreferenceManager

    private var binding: FragmentChatBinding? = null

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentTimeString: String
    private lateinit var messageTxt: String
    private var messageId: Int? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding?.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter =
            ChatAdapter(myPreferenceManager.getInt("UserID"), myPreferenceManager.getInt("GroupID"))

        val stringMessageItem = arguments?.getString("GroupMessages")
        val groupName = arguments?.getString("GroupName")
        val myType = object : TypeToken<MutableList<Message>>() {}.type
        val messages = Gson().fromJson<MutableList<Message>>(stringMessageItem, myType)

        binding?.groupNameText?.text = groupName

        binding?.recyclerView?.adapter = chatAdapter
        val mLayoutManager = LinearLayoutManager(binding?.recyclerView?.context)
        binding?.recyclerView?.layoutManager = mLayoutManager
        chatAdapter.setData(messages)
        binding?.recyclerView?.scrollToPosition(chatAdapter.itemCount - 1)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        currentTimeString = dateFormat.format(Date())

        if (messages != null && messages.size > 1) {
            messageId = messages[messages.size - 1].id?.plus(1)
        } else {
            Toast.makeText(context, "No Messages", Toast.LENGTH_SHORT).show()
        }

        binding?.scrollToDownArrow?.setOnClickListener() {
            binding?.recyclerView?.post {
                binding?.recyclerView?.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        }

        binding?.scrollToDownArrow?.visibility = View.GONE

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    binding?.scrollToDownArrow?.visibility = View.GONE
                } else {
                    val lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
                    if (lastVisibleItemPosition < chatAdapter.itemCount - 1) {
                        binding?.scrollToDownArrow?.visibility = View.VISIBLE
                    } else {
                        binding?.scrollToDownArrow?.visibility = View.GONE
                    }
                }

//
            }
        })

        if (binding?.messageEdit?.text.isNullOrEmpty()) {
//            binding?.pickImgBtn?.visibility = View.VISIBLE
            binding?.sendBtn?.visibility = View.VISIBLE
        } else {
            binding?.sendBtn?.visibility = View.VISIBLE
//            binding?.pickImgBtn?.visibility = View.INVISIBLE
        }

        binding?.sendBtn?.setOnClickListener {

            messageTxt = binding?.messageEdit?.text.toString().trim()

            val json = JSONObject().put("message", messageTxt)
//            groupWebSocket.send(json.toString())
            SocketService.sendMessage(messageTxt)
//            if(channelType != "user_channel" && groupId == myPreferenceManager.getInt("GroupID")){
//            chatAdapter.addItem(
//                Message(
//                    currentTimeString,
//                    messageId,
//                    messageTxt,
//                    Sender(
//                        null,
//                        null,
//                        myPreferenceManager.getInt("UserID"),
//                        null, null
//                    )
//                )
//            )}
            binding?.recyclerView?.smoothScrollToPosition(chatAdapter.itemCount - 1)
            resetMessageEdit()
        }

//        binding?.pickImgBtn?.setOnClickListener {
//            registerForActivityResult(
//                ActivityResultContracts.GetMultipleContents()
//            ) {
//
//            }
//        }
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
            val isRead = intent.getBooleanExtra("isRead", false)
            val senderId = intent.getIntExtra("senderId", 0)
            val groupId = intent.getIntExtra("groupId", 0)
            val channelType = intent.getStringExtra("type")
            val senderUserName = intent.getStringExtra("senderUserName")

            if (channelType != "user_channel" && groupId == myPreferenceManager.getInt("GroupID")) {
//                binding?.newMessageLayout?.visibility = View.VISIBLE
                chatAdapter.addItem(
                    Message(
                        currentTimeString,
                        messageId,
                        isRead,
                        message,
                        Sender(
                            null,
                            null,
                            senderId,
                            null,
                            senderUserName
                        )
                    )
                )
                binding?.recyclerView?.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val string: String = s.toString().trim()

        if (string.isEmpty()) {
            resetMessageEdit()
        } else {
            binding?.sendBtn?.visibility = View.VISIBLE
//            binding?.pickImgBtn?.visibility = View.INVISIBLE
        }
    }

    private fun resetMessageEdit() {
        binding?.messageEdit?.removeTextChangedListener(this)
        binding?.messageEdit?.setText("")
        binding?.sendBtn?.visibility = View.INVISIBLE
//        binding?.pickImgBtn?.visibility = View.VISIBLE
        binding?.messageEdit?.addTextChangedListener(this)
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}