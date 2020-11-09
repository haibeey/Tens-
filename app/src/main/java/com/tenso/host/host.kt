package com.tenso.host

import android.content.Context
import com.tenso.models.Broker
import com.tenso.models.FileTransmission
import com.tenso.models.TransferFile
import java.net.InetSocketAddress
import java.net.ServerSocket
import kotlin.collections.ArrayList


class Host(
    private var itemsToSend : ArrayList<TransferFile>,
    private val context: Context,
    ip: String,
    port: Int
) {
    private val hostSocket = ServerSocket()
    private var broker: Broker? = null

    fun getBroker():Broker?{
        return  broker
    }
    init {
        hostSocket.bind(InetSocketAddress(ip,port))
        startListening()
    }

    fun AddItemToSend(fileItem : TransferFile){
        itemsToSend.add(fileItem)
        if (broker!=null){
            broker?.itemsToSend?.add(fileItem)
        }
    }
    private fun startListening(){
        broker = Broker(hostSocket.accept(),itemsToSend,context,"HOST")
    }

    fun updateSendingItems(IS: ArrayList<TransferFile>){
        broker?.updateItemsToSend(IS)
    }

    fun send(IS : ArrayList<TransferFile>){
        itemsToSend = IS
        if (broker!=null){
            broker?.send(itemsToSend)
        }
    }

    fun receive(){
        if (broker!=null){
            broker?.receive()
        }
    }

    fun getSending(): ArrayList<FileTransmission>? {
        return  getBroker()?.getSending()
    }


    fun getReceiving(): ArrayList<FileTransmission>? {
        return  getBroker()?.getReceiving()
    }


    private fun Close(){
        hostSocket.close()
    }
}