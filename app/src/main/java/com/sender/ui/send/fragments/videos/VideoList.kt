package com.sender.ui.send.fragments.videos

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sender.R
import com.sender.ui.send.ItemSelection
import com.sender.ui.send.fragments.images.RvImagesItems
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VideoList : Fragment() {
    private var tracker: SelectionTracker<Long>? = null
    private var videoList = ArrayList<RvVideosItems>()
    private   val viewAdapter = VideoListAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewLayout =  inflater.inflate(R.layout.fragment_video_list, container, false)
        val viewManager = GridLayoutManager(context,2)
        val viewAdapter = VideoListAdapter(ArrayList())

        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState);
        }

        fun loadData(){
            GlobalScope.launch {
                val projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.MIME_TYPE
                )

                val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

                val query = context?.contentResolver?.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )
                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        val size = cursor.getInt(sizeColumn)

                        val rItem =RvVideosItems()
                        rItem.mimeType = mimeType
                        rItem.name=name
                        rItem.uri =  ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        rItem.size=size
                        videoList.add(rItem)
                    }
                }
                activity?.runOnUiThread {
                    viewAdapter.update(videoList)
                }
            }
        }

        if (videoList.isNotEmpty()){
            viewAdapter.update(videoList)
        }else{
            if (savedInstanceState != null) {
                try {
                    videoList =
                        savedInstanceState.getSerializable("video_data") as ArrayList<RvVideosItems>

                }catch (e : java.lang.Exception){
                    loadData()
                }
            }else{
                loadData()
            }
        }

        viewLayout.findViewById<RecyclerView>(R.id.media_items).apply {
            layoutManager = viewManager
            adapter = viewAdapter

            tracker = SelectionTracker.Builder<Long>(
                "mySelection", this, StableIdKeyProvider(this),
                ItemSelection(this), StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()

            viewAdapter.tracker = tracker
        }
        return viewLayout
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
        outState.putSerializable("video_data",videoList)
    }

    fun clearTracker(){
        try {
            viewAdapter.tracker?.clearSelection()
        }catch(e: Exception){}
    }
}
