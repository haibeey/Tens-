package com.sender.ui.send.fragments.audio

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sender.R
import com.sender.ui.send.ItemSelection
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileDescriptor


class AudioList : Fragment() {

    private var tracker: SelectionTracker<Long>? = null
    private var audioList = ArrayList<RvAudioItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewLayout =  inflater.inflate(R.layout.fragment_audio_list, container, false)
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = AudioListAdapter(ArrayList())

        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState);
        }

        fun loadData(){
            GlobalScope.launch {
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.MIME_TYPE
                )
                val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

                val query = context?.contentResolver?.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
                    val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)


                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val rItem = RvAudioItems()
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val size = cursor.getInt(sizeColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)
                        val artist = cursor.getString(artistColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
                            val options = BitmapFactory.Options()
                            try {
                                val sArtworkUri = Uri
                                    .parse("content://media/external/audio/albumart")
                                val uri =
                                    ContentUris.withAppendedId(sArtworkUri, id)
                                val pfd = context!!.contentResolver.openFileDescriptor(uri, "r")
                                if (pfd != null) {
                                    val fd: FileDescriptor? = pfd.fileDescriptor
                                    rItem.bitmap = BitmapFactory.decodeFileDescriptor(
                                        fd, null,
                                        options
                                    )
                                }
                            } catch (ee: Error) {
                            } catch (e: Exception) {
                            }
                        }

                        rItem.uri =  ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                            try {
                                rItem.bitmap=context?.applicationContext?.contentResolver?.loadThumbnail(
                                    rItem.uri!!, Size(1000, 1000), null)
                            }catch (e : Exception){

                            }
                        }
                        rItem.size=size
                        rItem.name = name
                        rItem.dateCreated = dateAdded
                        rItem.artist = artist
                        rItem.mimeType = mimeType
                        audioList.add(rItem)
                    }
                }

                activity?.runOnUiThread {
                    viewAdapter.update(audioList)
                }
            }
        }
        if (audioList.isNotEmpty()){
            viewAdapter.update(audioList)
        }else{
            if (savedInstanceState != null) {
                try {
                    audioList =
                        savedInstanceState.getSerializable("audio_data") as ArrayList<RvAudioItems>
                }catch (e : java.lang.Exception){
                    loadData()
                }
            }else{
                loadData()
            }

        }

        viewLayout.findViewById<RecyclerView>(R.id.audio_items).apply {
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
        outState.putSerializable("audio_data",audioList)
    }

}
