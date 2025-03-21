package com.home.reader.utils

import android.content.ClipData
import android.net.Uri
import java.io.File

private val regex = "[A-Za-z0-9\\s]+".toRegex()

fun ClipData.toUriList(): List<Uri> {
    return (0 until this.itemCount).map { getItemAt(it).uri }
}

fun String.toNormalizedName(): String {
    return regex.findAll(this)
        .map { it.value.trim() }
        .joinToString(" ")
        .lowercase()
}

fun File.resolve(id: Long): File = this.resolve(id.toString())