package com.sender.ui.send.fragments.docs

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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


class DocumentList : Fragment() {

    private var tracker: SelectionTracker<Long>? = null
    private var docList = ArrayList<RvDocItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewLayout =  inflater.inflate(R.layout.fragment_document_list, container, false)
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = DocListAdapter(ArrayList())

        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState);
        }

        fun loadData(){
            GlobalScope.launch {

                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.SIZE
                )

                val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('application/pdf') OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd%'"

                val sortOrder = "${ MediaStore.Files.FileColumns.DATE_ADDED} DESC"

                val query =context?.contentResolver?.query(
                    MediaStore.Files.getContentUri("external"),
                    projection,
                    whereClause,
                    null,
                    sortOrder
                )

                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

                    while (cursor.moveToNext()) {

                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        if (mimeType == "application/vnd.android.package-archive"){
                            continue
                        }

                        val rItem = RvDocItems()
                        rItem.uri =  ContentUris.withAppendedId(
                            MediaStore.Files.getContentUri("external"),
                            id
                        )
                        rItem.name = name
                        rItem.drawable_id=R.mipmap.ic_word_icon_forground
                        rItem.mimeType = mimeType
                        when(mimeType){
                            "application/pdf"->{
                                rItem.drawable_id=R.mipmap.ic_pdf_icon_foreground
                            }
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                                rItem.drawable_id=R.mipmap.ic_word_icon_forground
                            }
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                                rItem.drawable_id=R.mipmap.ic_office_icon_foreground
                            }
                        }
                        docList.add(rItem)
                    }
                }

                activity?.runOnUiThread {
                    viewAdapter.update(docList)
                }

            }
        }

        if (docList.isNotEmpty()){
            viewAdapter.update(docList)
        }else{
            if (savedInstanceState != null) {
                try {
                    docList =
                        savedInstanceState.getSerializable("doc_data") as ArrayList<RvDocItems>
                    Log.e("what",docList.toString())
                }catch (e : java.lang.Exception){
                    loadData()
                }
            }else{
                loadData()
            }

        }

        viewLayout.findViewById<RecyclerView>(R.id.doc_items).apply {
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
        outState.putSerializable("doc_data",docList)
    }


}
