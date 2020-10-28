package com.sender.client

import android.content.Context
import com.sender.models.Broker
import com.sender.models.FileTransmission
import com.sender.models.TransferFile
import com.sender.util.Utils
import com.sender.util.networkUtils
import java.net.InetSocketAddress
import java.net.Socket

class Client(
    private val itemsToSend : ArrayList<TransferFile>,
    private val context: Context,
    ip: String,
    port: Int
) {
    private val clientSocket = Socket()
    private var broker: Broker? = null

    init {
        clientSocket.connect(InetSocketAddress(ip, port))
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

    fun send(){
        if (broker!=null){
            broker?.send()
        }
    }


    fun getSending(): ArrayList<FileTransmission>? {
        return  getBroker()?.getSending()
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