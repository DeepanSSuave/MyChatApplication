package com.example.mychatapplication.model

import android.net.wifi.WifiManager.SubsystemRestartTrackingCallback
import androidx.annotation.DrawableRes
import com.example.mychatapplication.R

object Constants {

    var images = listOf(R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.image)
    val names = listOf("El Kamcy", "Aisha", "Joyce", "Martin")


    val users = listOf(
        User(images[0], names[0]),
        User(images[1], names[1]),
        User(images[2], names[2]),
        User(images[3], names[3]),
    )
}

data class User(
    @DrawableRes val image: Int,
    val name: String,
)