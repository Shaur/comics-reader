package com.home.reader.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.home.reader.utils.Constants.Dirs.COVERS
import java.io.File
import java.nio.file.Path

fun Activity.coversDir() = File("$filesDir/$COVERS")

fun Context.coversPath(): Path = dataPath().resolve(COVERS)

fun Context.dataPath(): Path = filesDir.toPath()

fun Context.getFileName(uri: Uri): String? {
    return contentResolver.query(uri, null, null, null, null)?.use {
        val nameIndex = it.getColumnIndex("_display_name")
        it.moveToFirst()
        it.getString(nameIndex)
    }
}