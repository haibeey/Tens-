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
    private var stream : InputStream?
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
        val res = ByteArray(count)
        try {
            if (stream==null)return res
            val off =stream!!.read(res,offset,count)
            offset+=off
        }catch (e : Exception){
            res.fill(65)
            offset+=res.size
            return  res
        }
        return  res
    }
}