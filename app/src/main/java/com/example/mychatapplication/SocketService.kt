package com.example.mychatapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mychatapplication.di.ChatPreferenceManager
import com.example.mychatapplication.view.DashBoardFragment
import com.example.mychatapplication.view.DashboardGroupAdapter
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SocketService : Service() {


    private lateinit var userWebSocket: WebSocket
    private lateinit var groupWebSocket: WebSocket

    @Inject
    lateinit var pref: ChatPreferenceManager

    companion object {

        private lateinit var client: OkHttpClient
        private lateinit var userWebSocket: WebSocket
        private lateinit var groupWebSocket: WebSocket
        private const val WEBSOCKET_USER_URL = "ws://192.168.5.42:8005/ws/user"

        fun startGroupChannel(groupId: Int, accessToken: String, context: Context) {
            if (this::groupWebSocket.isInitialized)
                groupWebSocket.cancel()

            val groupRequest = Request.Builder()
                .header("Authorization", accessToken)
                .url("ws://192.168.5.42:8005/ws/group/${groupId}")
                .build()

            groupWebSocket = client.newWebSocket(groupRequest, WebsocketListener(context))

        }

        fun sendMessage(message: String) {
            if (this::groupWebSocket.isInitialized) {
                val json = JSONObject().put("message", message)
                groupWebSocket.send(json.toString())
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startWebSocket()
        startForeground(11, createNotification())
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Group", channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startWebSocket() {
        client = OkHttpClient()

        val userRequest = Request.Builder()
            .header("Authorization", pref.getString("AccessToken"))
            .url(WEBSOCKET_USER_URL)
            .build()

        userWebSocket = client.newWebSocket(userRequest, WebsocketListener(applicationContext))
    }


    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, DashBoardFragment::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        return NotificationCompat.Builder(this, "Group")
            .setContentTitle("Foreground Service")
            .setContentText("Your service is running in the foreground.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        userWebSocket.cancel()
        groupWebSocket.cancel()
        client.dispatcher.executorService.shutdown()
    }

//    inner class WebsocketListener() : WebSocketListener() {
//        //    private val callback: WebsocketListenerCallback
//        override fun onOpen(webSocket: WebSocket, response: Response) {
//            super.onOpen(webSocket, response)
////        webSocket.send("Hey There ! Whats up?")
//            Log.e("socket", " Connected")
//        }
//
//        override fun onMessage(webSocket: WebSocket, text: String) {
//            output("Message Received : $text")
////        callback.onMessageReceived(text)
//        }
//
////    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
////        webSocket.close(NORMAL_CLOSURE_STATUS, null)
////        output("closing : $code / $reason")
////    }
//
//        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//            output("Error : " + t.message)
//        }
//
//        fun output(text: String) {
//            Log.d("Websocket",text)
//        }
//
//        companion object {
//            private const val NORMAL_CLOSURE_STATUS = 1000
//        }
//
//    }


}