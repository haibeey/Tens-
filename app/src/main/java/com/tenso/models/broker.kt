package com.tenso.models

import android.content.Context
import com.tenso.util.*
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.Charset

const val MAX_BYTE_SEND_RECEIVE_SIZE = 10000000

class Broker(private val socket : Socket,
             var itemsToSend : ArrayList<TransferFile>,
             val context : Context,
             private val type : String
){

    private val sending = ArrayList<FileTransmission>()
    private val receiving = ArrayList<FileTransmission>()
    private var receivingFailed = false
    var index = 0

    fun failedSending():Boolean{
        return receivingFailed
    }

    init {
        itemsToSend.forEach {
            sending.add(
                FileTransmission(
                    sizeSent = 0,
                    sending = true,
                    sizeToSend = it.size,
                    name = it.name,
                    mimeType = it.mimeType
                )
            )
        }
    }


    fun getSending():ArrayList<FileTransmission>{
        return sending
    }

    fun getReceiving():ArrayList<FileTransmission>{
        return receiving
    }

    fun updateItemsToSend(IS : ArrayList<TransferFile>){
        itemsToSend = IS
    }
    fun send(IS : ArrayList<TransferFile>){
        itemsToSend = IS
        itemsToSend.forEach {
            sending.add(
                FileTransmission(
                    sizeSent = 0,
                    sending = true,
                    sizeToSend = it.size,
                    name = it.name,
                    mimeType = it.mimeType
                )
            )
        }

        sendTransferList()
        while (itemsToSend.isNotEmpty()){
            val itemSending = itemsToSend.removeAt(0)
            val itemToSend = TransferEntity(
                commands = Defaults.Companion.Commands.TRANSFERITEM,
                item =  itemSending,
                size = 0,
                entitySize = FileUtils.fetchFileSize(itemSending.uri,context)
            )
            itemToSend.computeSize()

            val data = ByteBuffer.allocate(itemToSend.size+2*4)

            data.put(
                conversion.intToByteArray(
                    itemToSend.size
                )
            )
            data.put(
                conversion.intToByteArray(
                    itemToSend.commands.ordinal
                )
            )
            data.put(
                conversion.getGSONSerializer().toJson(itemToSend).toByteArray()
            )
            val outPutStream = socket.getOutputStream()
            outPutStream.write(
                data.array()
            )

            var dataSize = FileUtils.fetchFileSize(itemSending.uri,context)

            val fileReader = FileReader(itemSending.uri,context)
            while (dataSize>0){
                var goingToSend = MAX_BYTE_SEND_RECEIVE_SIZE
                if (dataSize<MAX_BYTE_SEND_RECEIVE_SIZE){
                    goingToSend = dataSize.toInt()
                }
                val sb = fileReader.take(goingToSend)

                sending[index].sizeSent+=sb.size
                dataSize-=sb.size
                outPutStream.write(
                    sb
                )
            }
            sending[index].sizeSent=sending[index].sizeToSend
            index++
        }
    }

    private fun sendTransferList(){
        val transferItem = TransferListEntity(
            commands = Defaults.Companion.Commands.TRANSFERLIST,
            items = itemsToSend,
            size = 0
        )
        transferItem.computeSize()

        val data = ByteBuffer.allocate(transferItem.size+2*4)

        data.put(
            conversion.intToByteArray(transferItem.size)
        )
        data.put(
            conversion.intToByteArray(transferItem.commands.ordinal)
        )

        data.put(
            conversion.getGSONSerializer().toJson(transferItem).toByteArray()
        )
        socket.getOutputStream().write(
            data.array()
        )
    }

    private fun sendNewTransfer(){

    }

    private fun receiveNewTransfer(){

    }

    fun receive(){
        var index = 0

        while (socket.isConnected){
            val inputStream = socket.getInputStream()
            val sizeByteArr = ByteArray(4)
            val  commandByteArr= ByteArray(4)
            if (inputStream.read(sizeByteArr)!=4){
                receivingFailed = true
                return
            }
            val size = conversion.byteArrayToInt(sizeByteArr)

            if (inputStream.read(commandByteArr)!=4){
                receivingFailed = true
                return
            }
            val command = conversion.byteArrayToInt(commandByteArr)
            if (command !in 2 downTo -1){
                receivingFailed = true
                return
            }

            var finishReceiving = false
            when (Defaults.Companion.Commands.values()[command]){
                Defaults.Companion.Commands.TRANSFERITEM->{
                    val entityArr = ByteArray(size = size)
                    if(inputStream.read(entityArr)!=size){
                        receiving[index].sending = false
                    }
                    val entity = conversion.getGSONDeSerializer().fromJson(
                        entityArr.toString(Charset.defaultCharset()),
                        TransferEntity::class.java
                    )

                    val fileWriter = FileWriter(entity.item.name,entity.item.mimeType,context)

                    while (entity.entitySize>0){
                        var goingToReceive = MAX_BYTE_SEND_RECEIVE_SIZE
                        if (entity.entitySize<MAX_BYTE_SEND_RECEIVE_SIZE){
                            goingToReceive = entity.entitySize.toInt()
                        }

                        val data =  ByteArray(size = goingToReceive)

                        val sizeRead = inputStream.read(data)
                        val readData = data.sliceArray(0 until sizeRead)
                        fileWriter.put(readData)
                        receiving[index].sizeSent+=sizeRead
                        entity.entitySize-=sizeRead
                    }
                    fileWriter.finish()
                    index++
                    if(index>=receiving.size){
                        finishReceiving = true
                        if (finishReceiving && Utils.testing){
                            return
                        }
                    }
                }
                Defaults.Companion.Commands.TRANSFERLIST->{
                    val itemList = ByteArray(size = size)
                    if(inputStream.read(itemList)!=size){
                        receivingFailed = true
                        return
                    }

                    val transferList = conversion.getGSONDeSerializer().fromJson(itemList.toString(Charset.defaultCharset()),TransferListEntity::class.java)

                    transferList.items.forEach {
                        receiving.add(
                            FileTransmission(
                                sizeSent = 0,
                                sending = true,
                                sizeToSend = it.size,
                                name = it.name,
                                mimeType = it.mimeType
                            )
                        )
                    }
                }
            }
        }
    }

}