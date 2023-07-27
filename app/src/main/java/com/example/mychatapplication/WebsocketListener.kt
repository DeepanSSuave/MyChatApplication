package com.example.mychatapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.mychatapplication.model.repositary.ReceivingMessage
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebsocketListener(private val context: Context) : WebSocketListener() {
//    private val callback: WebsocketListenerCallback
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
//        webSocket.send("Hey There ! Whats up?")
        Log.e("socket", " Connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("$webSocket Message Received : $text")

        val json = Gson().fromJson(text,ReceivingMessage::class.java)

        val intent = Intent("new_message_action")
        intent.putExtra("message", json.message)
        intent.putExtra("senderId",json.sender_id)
        intent.putExtra("type", json.type)
        intent.putExtra("groupId",json.group_id)
        intent.putExtra("senderUserName", json.sender_name)
        intent.putExtra("createdOn", json.created_on)
        context.sendBroadcast(intent)
//        callback.onMessageReceived(text)
    }

//    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//        webSocket.close(NORMAL_CLOSURE_STATUS, null)
//        output("closing : $code / $reason")
//    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    fun output(text: String) {
        Log.d("Websocket",text)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

}