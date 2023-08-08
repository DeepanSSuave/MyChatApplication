package com.example.mychatapplication.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapplication.R
import com.example.mychatapplication.`interface`.OnItemClickListener
import com.example.mychatapplication.model.repositary.GetMessageObjectItem
import com.example.mychatapplication.model.repositary.Message
import com.example.mychatapplication.model.repositary.ReceivingMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//private var clickedItemMessage : List<Message?> = emptyList()
//, private var groupName: (String) -> Unit
class DashboardGroupAdapter(private var groupId: (GetMessageObjectItem) -> Unit) :
    RecyclerView.Adapter<DashboardGroupAdapter.InnerViewHolder>() {

    private var groupList = arrayListOf<GetMessageObjectItem>()
    private var newMessagePosition: Int = -1
    private var fromPosition: Int = -1
//    private var newMessageList = emptyList<ReceivingMessage>()


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<GetMessageObjectItem>?) {
        if (list != null) {
            this.groupList = list
        }
        notifyDataSetChanged()
    }

    fun onNotificationReceive(lastMessage: ReceivingMessage) {
        val position = groupList.indexOf(groupList.find{ it.id == lastMessage.group_id })
        if(position == -1)
            return
        groupList[position].last_message = lastMessage.message
        val temp = groupList[position]
        groupList[position] = groupList[0]
        groupList[0] = temp
        notifyItemMoved(position, 0)
        newMessagePosition = 0
        notifyItemChanged(0)
    }

    inner class InnerViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val groupName: TextView = item.findViewById(R.id.groupNameText)
        val lastMessage: TextView = item.findViewById(R.id.lastMessageText)
//        val timeStamp : TextView = item.findViewById(R.id.timeText)


        @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
        fun setItems(item: GetMessageObjectItem, position: Int) {
//            var timeText = ""
            groupName.text = item.name
            lastMessage.text = item.last_message

            if (newMessagePosition == position) {
                fromPosition = position
                groupName.setTextColor(Color.BLACK)
                lastMessage.setTextColor(Color.BLACK)
            }
//            item.messages?.let { timeText = ((if(it.isNotEmpty()) ({{ it[it.size-1]?.created_at.toString()}}).toString() else TODO()).toString())}

//            val inputDateString = timeText
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
//            val outputFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
//
//            val date = inputFormat.parse(inputDateString)
//            timeStamp.text = date?.let { outputFormat.format(it) }.toString()
        }

        fun setNewMessage(item: ReceivingMessage) {
            lastMessage.text = item.message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.dashboard_rv_item, parent, false)
//        val lastMessage = view.findViewById<TextView>(R.id.lastMessageText)
//        val groupName = view.findViewById<TextView>(R.id.groupNameText)
        return InnerViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.setItems(groupList[position], position)
//        holder.setNewMessage()
        holder.itemView.setOnClickListener {
//            listener?.onItemClick(groupList[position].messages)
            groupId(groupList[position])
//            groupList[position].name?.let { groupName -> groupName(groupName) }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = groupList.size
}
