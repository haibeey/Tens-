package com.sender.ui.send.fragments.apps

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


class AppListAdapter(private val appList: ArrayList<RvAppItems>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long =  position.toLong()

    fun addTen(apps: ArrayList<RvAppItems>){
        apps.forEach {
            appList.add(it)
        }
        notifyDataSetChanged()
    }

    fun update(apps: ArrayList<RvAppItems>){
        appList.clear()
        appList.addAll(apps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_applist, parent, false) as View

        val width: Int =parent.measuredWidth / 4
        itemView.minimumWidth = width
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val rvItems = appList[position]
        val icon = holder.itemView.context!!.packageManager
            .getApplicationIcon(rvItems.packageName)

        Glide.with(holder.view)
            .load(icon)
            .fitCenter()
            .into(holder.view.findViewById(R.id.image_button))
        holder.view.findViewById<TextView>(R.id.app_name).text = rvItems.appName

        tracker?.let {
            val selected = tracker!!.isSelected(position.toLong())
            holder.itemView.isActivated=selected
            val activity = (holder.view.context as SendActivity)
            if (selected){
                activity.addToThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = rvItems.appName,
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "app",
                        mimeType = "application/vnd.android.package-archive"
                    )
                )

            }else{
                activity.removeFromThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = rvItems.appName,
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "app",
                        mimeType = "application/vnd.android.package-archive"
                    )
                )
            }
            activity.findViewById<MaterialButton>(R.id.sentItems).text=
                "SEND(${activity.sizeOfThingsToSend()})"
        }
    }

    override fun getItemCount() = appList.size
}