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

class NewGroupRVAdapter : RecyclerView.Adapter<NewGroupRVAdapter.InnerViewHolder>() {

    val imageList : List<Int> = Constants.images
    val textList : List<String> = Constants.names

    inner class InnerViewHolder(itemView : View) : ViewHolder(itemView){

        var imageV : ImageView = itemView.findViewById(R.id.innerImage)
        var nameText : TextView = itemView.findViewById(R.id.nameTV)

        fun setItems(image : Int, name : String ) {
            imageV.setImageResource(image)
            nameText.text = name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_newrv_item,parent,false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.setItems(imageList[position],textList[position])
    }

    override fun getItemCount(): Int = imageList.size
}