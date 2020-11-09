package com.tenso.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile


class FileUtils{
    companion object{

        var dir = "/sdcard/Tensõ/"
        fun fetchFileSize(uri : Uri,context: Context):Long{
            val returnCursor  = context.contentResolver.query(uri, null, null, null, null)
            val sizeIndex  = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
            returnCursor?.moveToFirst()
            val res = returnCursor?.getLong(sizeIndex!!)
            returnCursor?.close()
            if (res!=null)return  res
            val fileUri = uri.toFile()
            if (fileUri.exists())
                return fileUri.length()
            return  0
        }

        fun isPdf(name : String):Boolean{
            return name.endsWith("pdf")
        }
    }
}