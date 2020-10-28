package com.sender.ui.send.fragments.apps

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class RvAppItems(
    var appName: String = "",
    var uri: Uri? = null,
    var packageName: String = ""
): Parcelable