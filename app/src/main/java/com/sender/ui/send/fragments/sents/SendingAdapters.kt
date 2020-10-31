package com.sender.ui.send.fragments.sents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sender.R
import com.sender.models.FileTransmission
import com.sender.ui.send.BaseViewHolder
import com.sender.ui.send.fragments.images.RvImagesItems

class SendingAdapters() :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var dataItems = ArrayList<FileTransmission>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_transfer, parent, false) as View

        return BaseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    fun updateData(data : ArrayList<FileTransmission>?){
        if (data==null)return
        dataItems = data
        notifyDataSetChanged()
    }

    fun getData():ArrayList<FileTransmission>{
        return dataItems
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val fileTrans = dataItems[position]
        holder.itemView.findViewById<TextView>(R.id.name).text = fileTrans.name
        holder.itemView.findViewById<TextView>(R.id.percent).text = fileTrans.sizeSent.toString()
    }
}