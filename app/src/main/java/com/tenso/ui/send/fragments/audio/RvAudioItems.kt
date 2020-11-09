package com.tenso.ui.send.fragments.audio

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RvAudioItems(
    var size: Int = 0,
    var dateCreated : Long = 0,
    var uri : Uri? = null,
    var name  : String? = null,
    var artist  : String? = null,
    var art  : String? = null,
    var bitmap : Bitmap? = null,
    var mimeType : String? = null
): Parcelable

