package com.tenso.ui.send.fragments.apps

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RvAppItems(
    var appName: String = "",
    var uri: Uri? = null,
    var packageName: String = ""
): Parcelable