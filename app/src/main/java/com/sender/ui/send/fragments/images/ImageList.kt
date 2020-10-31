package com.sender.ui.send.fragments.images


import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sender.R
import com.sender.ui.send.ItemSelection
import com.sender.ui.send.fragments.audio.RvAudioItems
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageList : Fragment(){
    private var tracker: SelectionTracker<Long>? = null
    private var imageList = ArrayList<RvImagesItems>()
    private   val viewAdapter = ImageListAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewLayout =  inflater.inflate(R.layout.fragment_image_list, container, false)
        val viewManager = GridLayoutManager(context,3)


        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState);
        }

        viewLayout.findViewById<SeekBar>(R.id.seek_bar).setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                viewManager.scrollToPosition(((progress/100.0)*viewAdapter.getItemSize()).toInt())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        fun loadData(){
            GlobalScope.launch {
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.MIME_TYPE
                )

                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

                val query = context?.contentResolver?.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                    val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val size = cursor.getInt(sizeColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)

                        val rItem = RvImagesItems()

                        rItem.uri =  ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        rItem.size=size
                        rItem.name = name
                        rItem.dateCreated = dateAdded
                        rItem.mimeType = mimeType
                        imageList.add(rItem)
                    }
                }

                activity?.runOnUiThread {
                    viewAdapter.update(imageList)
                }
            }
        }

        if (imageList.isNotEmpty()){
            viewAdapter.update(imageList)
        }else{
            if (savedInstanceState != null) {
                try {
                    imageList =
                        savedInstanceState.getSerializable("image_data") as ArrayList<RvImagesItems>

                }catch (e : java.lang.Exception){
                    loadData()
                }
            }else{
                loadData()
            }
        }
        viewLayout.findViewById<RecyclerView>(R.id.image_list).apply {
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
        tracker?.onSaveInstanceState(outState);
        outState.putSerializable("image_data",imageList)
    }

    fun clearTracker(){
        try {
            viewAdapter.tracker?.clearSelection()
        }catch(e: Exception){}
    }

}
