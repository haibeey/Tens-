package com.sender.ui.send.fragments.sents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sender.R
import com.sender.models.FileTransmission
import com.sender.ui.send.BaseViewHolder
import com.sender.ui.send.ProgressSendingReceiving
import com.sender.ui.send.fragments.images.RvImagesItems
import com.sender.util.conversion

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
        holder.itemView.findViewById<TextView>(R.id.percent).text = conversion.byteToMbLong(fileTrans.sizeSent)
        val updatePercentage = ((fileTrans.sizeSent*1f)/fileTrans.sizeToSend)*100
        (holder.itemView as ProgressSendingReceiving).updateProgress(updatePercentage)

        var drawable = R.drawable.ic_android_black_24dp
        when (fileTrans.mimeType){
            "application/vnd.android.package-archive"->{
                drawable = R.drawable.ic_android_black_24dp
            }
            "application/pdf"->{
                drawable = R.drawable.ic_note_black_24dp
            }
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                drawable = R.drawable.ic_note_black_24dp
            }

            "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                drawable = R.drawable.ic_note_black_24dp
            }
            "audio/mpeg"->{
                drawable = R.drawable.ic_audiotrack_black_24dp
            }
            "image/jpeg"->{
                drawable = R.drawable.ic_image_black_24dp
            }
            "image/png"->{
                drawable = R.drawable.ic_image_black_24dp
            }

            "video/mp4"->{
                drawable = R.drawable.ic_videocam_black_24dp
            }
            "video/x-matroska"->{
                drawable = R.drawable.ic_videocam_black_24dp
            }
        }

        Glide.with(holder.view)
            .load(drawable)
            .fitCenter()
            .into(holder.view.findViewById(R.id.img_file_type))
    }
}