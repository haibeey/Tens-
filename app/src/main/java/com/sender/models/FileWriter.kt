package com.sender.models

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class FileWriter(val uri: Uri,context: Context,mimeType: String){
    private var stream : OutputStream?
    private var folderPath : String = getFolderPath(mimeType)
    init {
        val resolver = context.applicationContext.contentResolver
        stream = resolver.openOutputStream(uri)
    }


    fun put(bytes: ByteArray){
      stream?.write(bytes)
    }

    private fun getFolderPath(mimeType: String):String{
        var folder = "/sdcard/Tensõ/"
        when (mimeType){
            "application/vnd.android.package-archive"->{
                folder = "/sdcard/Tensõ/App/"
            }
            "application/pdf"->{
                folder = "/sdcard/Tensõ/Doc/"
            }
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"->{
                folder = "/sdcard/Tensõ/Doc/"
            }

            "application/vnd.openxmlformats-officedocument.presentationml.presentation"->{
                folder = "/sdcard/Tensõ/Doc/"
            }
            "audio/mpeg"->{
                folder = "/sdcard/Tensõ/Audio/"
            }
            "image/jpeg"->{
                folder = "/sdcard/Tensõ/Images/"
            }
            "image/png"->{
                folder = "/sdcard/Tensõ/Images/"
            }

            "video/mp4"->{
                folder = "/sdcard/Tensõ/Videos/"
            }
            "video/x-matroska"->{
                folder = "/sdcard/Tensõ/Videos/"
            }
        }
        return  folder
    }

}