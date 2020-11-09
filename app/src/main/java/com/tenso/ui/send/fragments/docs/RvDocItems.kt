package com.tenso.ui.send.fragments.docs


import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RvDocItems (
    var size: Int = 0,
    var dateCreated : Long = 0,
    var uri : Uri? = null,
    var name  : String? = null,
    var drawable_id  : Int = 0,
    var mimeType  : String?= null
) : Parcelable