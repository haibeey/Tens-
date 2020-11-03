package com.sender.models

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.sender.util.Utils
import java.io.File
import java.io.InputStream
import java.lang.Exception


class FileReader(uri : Uri, context: Context){
    private val stream : InputStream?
    init {
        val resolver = context.applicationContext.contentResolver
        stream = resolver.openInputStream( uri)
    }
    private var availableToRead = true
    private var offset = 0


    fun canRead():Boolean{
        return availableToRead
    }
    fun take(count : Int):ByteArray{
        var res = ByteArray(count)
//        if (stream==null)return res
//        val off =stream.read(res,offset,count)
//        offset+=off
//        res = res.sliceArray(0 until offset)
//        return  res
        try {
            if (stream==null)return res
            val off =stream.read(res,offset,count)
            offset+=off
            res = res.sliceArray(0 until offset)
        }catch (e : Exception){
            res.fill(65)
            offset+=res.size
            Utils.printItems(e)
            return  res
        }
        return  res
    }
}