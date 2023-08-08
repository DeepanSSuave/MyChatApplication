package com.example.mychatapplication.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mychatapplication.R
import com.example.mychatapplication.model.repositary.Message
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val userId: Int, private val groupID : Int) : RecyclerView.Adapter<ViewHolder>() {

    private val TYPEMESSAGESENT = 0
    private val TYPEMESSAGERECEIVED = 1
    private val TYPEIMAGESENT = 2
    private val TYPEIMAGERECEIVED = 3
    private val TYPENEWMESSAGE = 4

    private var newMessageView: Boolean = false
    private var canShowNewMessageView: Boolean = true

    private var messages = mutableListOf<Message>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<Message>) {
        this.messages = list
        notifyDataSetChanged()
    }

    private class SentImageHolder(itemView: View) : ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    private class SentMessageHolder(itemView: View) : ViewHolder(itemView) {
        var messageTxt: TextView = itemView.findViewById(R.id.sentMessage)
        val timeStamp : TextView = itemView.findViewById(R.id.timeTextSentMessageLayout)
    }

    private class ReceivedMessageHolder(itemView: View) :
        ViewHolder(itemView) {
        var nameTxt: TextView = itemView.findViewById(R.id.nameTxt)
        var messageTxt: TextView = itemView.findViewById(R.id.receivedTxt)
        var newMessageLayout: View = itemView.findViewById(R.id.newMessageLayout)
        val timeStamp : TextView = itemView.findViewById(R.id.timeTextReceivedMessageLayout)
    }

    private class ReceivedImageHolder(itemView: View) :
        ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var nameTxt: TextView = itemView.findViewById(R.id.nameTxt)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        try {
            return if (message.sender?.id == userId) {
                if (message.message != null)
                    TYPEMESSAGESENT else
                    TYPEIMAGESENT
            } else {
                if (message.message != null) {
                    newMessageView = true
                    TYPEMESSAGERECEIVED
                } else TYPEIMAGERECEIVED
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View

        when (viewType) {
            TYPEMESSAGESENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sent_meesage, parent, false)
                return SentMessageHolder(view)
            }
            TYPEMESSAGERECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_received_message, parent, false)
                return ReceivedMessageHolder(view)
            }
            TYPEIMAGESENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sent_image, parent, false)
                return SentImageHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_received_image, parent, false)
                return ReceivedImageHolder(view)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = messages[position]

        try {
            if (message.sender?.id == userId) {
                if (message.message == null) {
                    val imageHolder = holder as SentImageHolder
                    val bitmap: Bitmap = getBitmapFromString(message.message)
                    imageHolder.imageView.setImageBitmap(bitmap)
                } else {
                    val messageHolder = holder as SentMessageHolder
                    messageHolder.messageTxt.text = message.message
                    val timeString = message.created_at.toString()
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
                    val date = inputFormat.parse(timeString)
                    messageHolder.timeStamp.text = date?.let { outputFormat.format(it) }.toString()
                }
            } else {
                if (message.message == null) {
                    val imageHolder = holder as ReceivedImageHolder
                    imageHolder.nameTxt.text = message.sender?.username.toString()
                    val bitmap: Bitmap = getBitmapFromString(message.message)
                    imageHolder.imageView.setImageBitmap(bitmap)
                } else {
                    val messageHolder = holder as ReceivedMessageHolder
                    messageHolder.nameTxt.text = message.sender?.username.toString()
                    messageHolder.messageTxt.text = message.message
                    val timeString = message.created_at.toString()
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
                    val date = inputFormat.parse(timeString)
                    messageHolder.timeStamp.text = date?.let { outputFormat.format(it) }.toString()
                    if(canShowNewMessageView && !message.isRead) {
                        canShowNewMessageView = false
                        messageHolder.newMessageLayout.visibility = View.VISIBLE
                    } else {
                        messageHolder.newMessageLayout.visibility = View.GONE
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromString(message: String?): Bitmap {
        val byteArray: ByteArray = Base64.decode(message, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(
            byteArray, 0,
            byteArray.size
        )
    }

    override fun getItemCount(): Int = messages.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(jsonObject: Message) {
        canShowNewMessageView = true
        messages.add(jsonObject)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewMessage(jsonObject: Message, groupId: Int) {
        if (jsonObject.sender?.id != userId && groupId == groupID) {
            messages.add(jsonObject)
            notifyDataSetChanged()
        }
    }
}
