package com.tenso.models

import android.content.Context
import android.net.Uri
import java.io.IOException
import java.io.InputStream


class FileReader(uri : Uri, context: Context){
    private val stream : InputStream?
    init {
        val resolver = context.applicationContext.contentResolver
        stream = resolver.openInputStream( uri)
    }
    private var availableToRead = true

    fun canRead():Boolean{
        return availableToRead
    }
    fun take(count : Int):ByteArray{
        if (!canRead()){
            return  ByteArray(0)
        }
        var res = ByteArray(count)
        if (stream==null)return res
        return try {
            val off =stream.read(res)
            if (off==-1){
                availableToRead=false
            }
            res = res.sliceArray(0 until off)
            res
        }catch (e : IOException){
            ByteArray(0)
        }
    }
}