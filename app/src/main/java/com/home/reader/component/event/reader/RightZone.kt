package com.home.reader.component.event.reader

import android.view.View

class RightZone(
    priority: Int,
    handler: () -> Unit
) : ReaderZone(priority, handler) {

    override fun check(v: View, x: Float, y: Float): Boolean {
        return x > v.width / 2
    }
}