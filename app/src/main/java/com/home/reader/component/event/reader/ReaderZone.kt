package com.home.reader.component.event.reader

import android.view.View

abstract class ReaderZone(
    val priority: Int,
    private val handler: () -> Unit
) {

    private var zone: ReaderZone? = null

    fun setNext(zone: ReaderZone): ReaderZone? {
        this.zone = zone
        return this.zone
    }

    fun handle(v: View, x: Float, y: Float) {
        if(check(v, x, y)) {
            handler.invoke()
        } else {
            zone?.handle(v, x, y)
        }
    }

    protected abstract fun check(v: View, x: Float, y: Float): Boolean
}