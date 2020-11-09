package com.tenso.ui.send.fragments.images


import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class RvImagesItems (
    var size: Int = 0,
    var dateCreated : Long = 0,
    var uri : Uri? = null,
    var name  : String? = null,
    var mimeType  : String? = null
) : Parcelable