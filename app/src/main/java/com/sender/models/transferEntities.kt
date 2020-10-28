package com.sender.models

import com.google.gson.Gson
import com.sender.util.Defaults
import com.sender.util.conversion


data class TransferListEntity(
    val commands: Defaults.Companion.Commands,
    val items: ArrayList<TransferFile>,
    var size: Int
){
    fun computeSize(){
        this.size= conversion.getGSONSerializer().toJson(this).length
        this.size= conversion.getGSONSerializer().toJson(this).length
    }
}

data class TransferEntity(
    val commands: Defaults.Companion.Commands,
    val item: TransferFile,
    var size: Int,
    var entitySize: Long
){
    fun computeSize(){
        this.size= conversion.getGSONSerializer().toJson(this).length
        this.size= conversion.getGSONSerializer().toJson(this).length
    }
}