package com.sender.ui.send.fragments.sents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sender.R


class SentList : Fragment() {


    private val sendingAdapter = SendingAdapters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewLayout = inflater.inflate(R.layout.fragment_sent_list, container, false)
        val linearLayoutManager = LinearLayoutManager(viewLayout.context)

        viewLayout.findViewById<RecyclerView>(R.id.sending_list).apply {
            layoutManager = linearLayoutManager
            adapter = sendingAdapter
        }
        return  viewLayout

    }

    fun getSendingAdapter():SendingAdapters{
        return  sendingAdapter
    }

}
