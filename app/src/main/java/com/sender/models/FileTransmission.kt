package com.sender.models

data class FileTransmission(
    val sizeToSend : Long,
    var sizeSent : Long,
    var sending : Boolean,
    var name : String,
    var mimeType : String
)