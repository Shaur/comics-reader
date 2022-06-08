package com.home.reader.component.event.reader

import android.view.View

class DeadZone(priority: Int, handler: () -> Unit) : ReaderZone(priority, handler) {

    override fun check(v: View, x: Float, y: Float): Boolean {
        val deadZoneW = v.width * 0.05
        val deadZoneH = v.height * 0.1

        return (x < deadZoneW || x > v.width - deadZoneW) || y > v.height - deadZoneH
    }

}