package com.home.reader.component.event.reader

import android.view.View

class CenterZone(priority: Int, handler: () -> Unit) : ReaderZone(priority, handler) {

    override fun check(v: View, x: Float, y: Float): Boolean {
        val left = v.width / 2 - v.width * 0.1
        val right = v.width / 2 + v.width * 0.1

        val top = v.height / 2 + v.height * 0.1
        val bottom = v.height / 2 - v.height * 0.1

        return ((x in left..right) && (y in bottom..top))
    }

}