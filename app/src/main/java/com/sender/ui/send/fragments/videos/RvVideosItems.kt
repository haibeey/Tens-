package com.sender.ui.send.fragments.videos

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RvVideosItems(
    var name: String? = null,
    var duration: Int = 0,
    var size: Int = 0,
    var uri: Uri? = null,
    var mimeType: String? = null
) : Parcelable
