package com.sender.ui.send.fragments.images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class ImageListAdapter(private val imageList: ArrayList<RvImagesItems>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long =  position.toLong()

    fun addTen(apps: ArrayList<RvImagesItems>){
        apps.forEach {
            imageList.add(it)
        }
        notifyDataSetChanged()
    }

    fun update(imgs: ArrayList<RvImagesItems>){
        imageList.clear()
        imageList.addAll(imgs)
        notifyDataSetChanged()
    }

    fun getSelectionTracker():SelectionTracker<Long>?{
        return  tracker
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_image, parent, false) as View

        val width: Int =parent.measuredWidth / 3
        itemView.minimumWidth = width
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val rvItems = imageList[position]
        Glide.with(holder.view)
            .load(rvItems.uri)
            .placeholder(R.drawable.ic_spinner_1s_200px)
            .into(holder.view.findViewById(R.id.image_button_img))

        tracker?.let {
            val selected = tracker!!.isSelected(position.toLong())
            holder.itemView.isActivated=selected
            val activity = (holder.view.context as SendActivity)
            if (selected){
                activity.addToThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "images",
                        mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                    )
                )
            }else{
                activity.removeFromThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "images",
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
