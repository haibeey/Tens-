package com.sender.models

import android.net.Uri

data class TransferFile (
 val name : String,
 val uri : Uri,
 val size : Long,
 val type : String,
 val mimeType : String
){
 override fun equals(other: Any?): Boolean {
  if (other==null)return  false
  if (other !is TransferFile)return false
  if (this.uri==other.uri)return true
  return false
 }

 override fun hashCode(): Int {
  return uri.hashCode() ?: 0
 }

}