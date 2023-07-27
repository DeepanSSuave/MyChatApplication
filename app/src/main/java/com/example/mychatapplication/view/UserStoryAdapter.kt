package com.example.mychatapplication.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mychatapplication.R
import com.example.mychatapplication.model.Constants
import com.example.mychatapplication.model.User

class UserStoryAdapter : RecyclerView.Adapter<UserStoryAdapter.InnerViewHolder>() {

    private var users = emptyList<User>()

    fun setStory(users: List<User>) {
        this.users = users
    }

    inner class InnerViewHolder(item: View) : ViewHolder(item) {

        private val innerImage: ImageView = item.findViewById(R.id.innerImage)
        private val nameText: TextView = item.findViewById(R.id.nameTV)

        fun setData(user: User) {
            nameText.text = user.name
            innerImage.setImageResource(user.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_newrv_item, parent, false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.setData(users[position])
    }

    override fun getItemCount(): Int = Constants.images.size

}