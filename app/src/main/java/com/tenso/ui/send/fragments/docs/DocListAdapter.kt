package com.tenso.ui.send.fragments.docs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.tenso.R
import com.tenso.models.TransferFile
import com.tenso.ui.send.BaseViewHolder
import com.tenso.ui.send.SendActivity
import com.tenso.util.FileUtils
import com.tenso.util.Utils

class DocListAdapter(private val docList: ArrayList<RvDocItems>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long =  position.toLong()

    fun addTen(apps: ArrayList<RvDocItems>){
        apps.forEach {
            docList.add(it)
        }
        notifyDataSetChanged()
    }

    fun update(audios: ArrayList<RvDocItems>){
        docList.clear()
        docList.addAll(audios)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_doc_list, parent, false) as View

        return BaseViewHolder(itemView)
    }
    fun getSelectionTracker():SelectionTracker<Long>?{
        return  tracker
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val rvItems = docList[position]

        holder.itemView.findViewById<TextView>(R.id.name).text = Utils.cutShort(rvItems.name)

        Glide.with(holder.view)
            .load(rvItems.uri)
            .placeholder(rvItems.drawable_id)
            .into(holder.view.findViewById(R.id.doc_img))

        tracker?.let {
            val selected = tracker!!.isSelected(position.toLong())
            holder.itemView.isActivated=selected
            val activity = (holder.view.context as SendActivity)
            if (selected){
                activity.addToThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "doc",
                        mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                    )
                )
            }else{
                activity.removeFromThingsToSend(
                    TransferFile(uri = rvItems.uri!!,
                        name = Utils.nullOrEmptyString(rvItems.name),
                        size = FileUtils.fetchFileSize(rvItems.uri!!,holder.itemView.context),
                        type = "doc",
                        mimeType = Utils.nullOrEmptyString(rvItems.mimeType)
                    )
                )
            }
            activity.findViewById<MaterialButton>(R.id.sentItems).text=
                "SEND(${activity.sizeOfThingsToSend()})"
        }

    }
    fun getItemSize():Int{
        return docList.size
    }

    override fun getItemCount() = docList.size
}
