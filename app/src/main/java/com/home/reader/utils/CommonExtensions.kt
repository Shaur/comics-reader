package com.home.reader.utils

import android.content.ClipData
import android.net.Uri

fun ClipData.toUriList(): List<Uri> {
    return (0 until this.itemCount).map { getItemAt(it).uri }
}