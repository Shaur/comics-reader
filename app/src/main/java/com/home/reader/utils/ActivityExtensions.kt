package com.home.reader.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.DisplayMetrics
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.home.reader.persistence.AppDatabase
import com.home.reader.persistence.dao.IssueDao
import com.home.reader.persistence.dao.SeriesDao
import com.home.reader.utils.Constants.Dirs.COVERS
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun Context.issueDao(): IssueDao {
    return AppDatabase.invoke(this).issueDao()
}

fun Context.seriesDao(): SeriesDao {
    return AppDatabase.invoke(this).seriesDao()
}

fun Context.widthInDp(): Float {
    val displayMetrics: DisplayMetrics = applicationContext.resources.displayMetrics
    return displayMetrics.widthPixels / displayMetrics.density
}

fun Context.dataDir() = File("$filesDir/$packageName")

fun Activity.coversDir() = File("$filesDir/$packageName/$COVERS")

fun Context.coversPath(): Path = dataPath().resolve(COVERS)

fun Context.dataPath(): Path = Paths.get(filesDir.absolutePath, packageName)

fun Context.getFileName(uri: Uri): String? {
    return contentResolver.query(uri, null, null, null, null)?.use {
        val nameIndex = it.getColumnIndex("_display_name")
        it.moveToFirst()
        it.getString(nameIndex)
    }
}

fun Activity.checkReadStoragePermission() {
    val checkPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.RequestCodes.READ_EXTERNAL_STORAGE_CODE
        )
    }
}