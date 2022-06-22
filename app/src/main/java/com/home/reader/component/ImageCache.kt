package com.home.reader.component

import android.graphics.Bitmap
import android.util.LruCache
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ImageCache {
    val cache: LruCache<Long, Bitmap>
    val imageLoaderExecutor: ExecutorService = Executors.newFixedThreadPool(3)

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        cache = object : LruCache<Long, Bitmap>(cacheSize) {

            override fun sizeOf(key: Long, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }
}