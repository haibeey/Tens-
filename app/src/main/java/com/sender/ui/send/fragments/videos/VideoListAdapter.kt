package com.sender.ui.send.fragments.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.sender.R
import com.sender.models.TransferFile
import com.sender.ui.send.BaseViewHolder
import com.sender.ui.send.SendActivity
import com.sender.util.FileUtils
import com.sender.util.Utils
import com.sender.util.conversion


class VideoListAdapter(private val videoList: ArrayList<RvVideosItems>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long =  position.toLong()

    fun update(videos: ArrayList<RvVideosItems>){
        videoList.clear()
        videoList.addAll(videos)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_video, parent, false) as View

        return BaseViewHolder(itemView)
    }

    fun getSelectionTracker():SelectionTracker<Long>?{
        return  tracker
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val rvItems = videoList[position]
        Glide.with(holder.view)
            .load(rvItems.uri)
            .into(holder.view.findViewById(R.id.image_button))
        holder.view.findViewById<TextView>(R.id.video_name).text = Utils.removeExt(rvItems.name)
        holder.view.findViewById<TextView>(R.id.video_size).text = conversion.byteToMb(rvItems.size)

        tracker?.let {

            val selected = tracker!!.isSelected(position.toLong())
            val imgView = holder.view.findViewById<ImageView>(R.id.vid_selected)
            imgView.isActivated=selected
            val activity =  (imgView.context as SendActivity)
            if (selected){
               activity.addToThingsToSend(
                   TransferFile(uri = rvItems.uri!!,
                       name = Utils.nullOrEmptyString(rvItems.name),
                       size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                       type = "videos",
                       mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                   )
               )
            }else{
                activity.removeFromThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "videos",
                        mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                    )
                )
            }
            activity.findViewById<MaterialButton>(R.id.sentItems).text=
                "SEND(${activity.sizeOfThingsToSend()})"
        }
    }

    override fun getItemCount() = videoList.size
}