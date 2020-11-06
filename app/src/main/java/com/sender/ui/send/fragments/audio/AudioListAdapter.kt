package com.sender.ui.send.fragments.audio

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class AudioListAdapter(private val imageList: ArrayList<RvAudioItems>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long =  position.toLong()

    fun addTen(apps: ArrayList<RvAudioItems>){
        apps.forEach {
            imageList.add(it)
        }
        notifyDataSetChanged()
    }

    fun update(audios: ArrayList<RvAudioItems>){
        imageList.clear()
        imageList.addAll(audios)
        notifyDataSetChanged()
    }

    fun getSelectionTracker():SelectionTracker<Long>?{
        return  tracker
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_audiolist, parent, false) as View

        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val rvItems = imageList[position]


        holder.itemView.findViewById<TextView>(R.id.artist).text = rvItems.artist
        holder.itemView.findViewById<TextView>(R.id.audio_name).text = Utils.stripAudioName(rvItems.name)

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            Glide.with(holder.view)
                .load(rvItems.bitmap)
                .placeholder(R.drawable.ic_music_note_black_24dp)
                .into(holder.view.findViewById(R.id.audio_img))
        }else{
            Glide.with(holder.view)
                .load(rvItems.art)
                .placeholder(R.drawable.ic_music_note_black_24dp)
                .into(holder.view.findViewById(R.id.audio_img))
        }

        tracker?.let {
            val selected = tracker!!.isSelected(position.toLong())
            holder.itemView.isActivated=selected
            val activity = (holder.view.context as SendActivity)
            if (selected){
                activity.addToThingsToSend(TransferFile(uri = rvItems.uri!!,
                    name = rvItems.name!!,
                    size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                    type = "audio",
                    mimeType = rvItems.mimeType!!
                ))
            }else{
                activity.removeFromThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "audio",
                        mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                    )
                )
            }
            activity.findViewById<MaterialButton>(R.id.sentItems).text=
                "SEND(${activity.sizeOfThingsToSend()})"
        }

    }
    fun getItemSize():Int{
        return imageList.size
    }

    override fun getItemCount() = imageList.size
}
