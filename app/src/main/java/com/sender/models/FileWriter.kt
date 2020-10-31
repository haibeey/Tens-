package com.sender.models

import android.app.Activity
import android.content.Context
import android.util.Log
import com.sender.util.FileUtils
import com.sender.util.Utils
import java.io.File
import java.io.OutputStream

class FileWriter(val name: String,mimeType: String,context : Context){
    private var stream : OutputStream?
    private var folderPath : String = getFolderPath(mimeType)
    init {
        val file = File(folderPath+name)

        Utils.printItems(file.name,file.absolutePath)

        file.createNewFile()

        stream = file.outputStream()
    }


    fun put(bytes: ByteArray){
      stream?.write(bytes)
    }

    private fun getFolderPath(mimeType: String):String{
        var folder = FileUtils.dir
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