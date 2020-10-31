package com.sender.ui.send.fragments.received

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sender.R
import com.sender.ui.send.ItemSelection


class ReceiveList : Fragment() {

    private val receivingAdapter = ReceivingAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewLayout = inflater.inflate(R.layout.fragment_receive_list, container, false)
        val linearLayoutManager = LinearLayoutManager(viewLayout.context)

        viewLayout.findViewById<RecyclerView>(R.id.receiving_list).apply {
            layoutManager = linearLayoutManager
            adapter = receivingAdapter
        }
        return  viewLayout
    }

    fun getReceivingAdapter():ReceivingAdapter{
        return  receivingAdapter
    }

}
