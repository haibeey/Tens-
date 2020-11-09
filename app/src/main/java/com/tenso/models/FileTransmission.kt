package com.tenso.models

data class FileTransmission(
    val sizeToSend : Long,
    var sizeSent : Long,
    var sending : Boolean,
    var name : String,
    var mimeType : String
){
    override fun equals(other: Any?): Boolean {
        if (other==null)return  false
        if (other !is FileTransmission)return false
        if (this.hashCode()==other.hashCode())return true
        return false
    }

    override fun hashCode(): Int {
        var result = sizeToSend.hashCode()
        result = 31 * result + sizeSent.hashCode()
        result = 31 * result + sending.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}