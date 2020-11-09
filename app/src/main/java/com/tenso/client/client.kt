package com.tenso.client

import android.content.Context
import com.tenso.models.Broker
import com.tenso.models.FileTransmission
import com.tenso.models.TransferFile
import java.net.InetSocketAddress
import java.net.Socket

class Client(
    private var itemsToSend : ArrayList<TransferFile>,
    private val context: Context,
    ip: String,
    port: Int
) {
    private val clientSocket = Socket()
    private var broker: Broker? = null

    init {
        clientSocket.connect(InetSocketAddress(ip, port),3000)
        startListening()
    }

    fun getBroker():Broker?{
        return  broker
    }
    fun AddItemToSend(fileItem : TransferFile){
        itemsToSend.add(fileItem)
        if (broker!=null){
            broker?.itemsToSend?.add(fileItem)
        }
    }
    private fun startListening(){
        broker = Broker(clientSocket,itemsToSend,context,"CLIENT")
    }

    fun send(IS : ArrayList<TransferFile>){
        itemsToSend = IS
        if (broker!=null){
            broker?.send(itemsToSend)
        }
    }


    fun getSending(): ArrayList<FileTransmission>? {
        return  getBroker()?.getSending()
    }


    fun updateSendingItems(IS: ArrayList<TransferFile>){
        broker?.updateItemsToSend(IS)
    }

    fun getReceiving(): ArrayList<FileTransmission>? {
        return  getBroker()?.getReceiving()
    }

    fun receive(){
        if (broker!=null){
            broker?.receive()
        }
    }

    private fun Close(){
        clientSocket.close()
    }

}