package com.home.reader.component.event.reader

import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import kotlin.math.abs

class OnLeafListener(
    private val currentPage: LiveData<Int>,
    private val pagesCount: Int,
    private var onSubPageLoad: (Int) -> Unit = {},
    private var onSwipeLeft: () -> Unit = {},
    private var onSwipeRight: () -> Unit = {}
) : View.OnTouchListener {

    private var baseX: Float = 0f
    private var isImageLoaded = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            baseX = event.rawX
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            if(!actionMove(view, event)) return false
        }

        if (event.action == MotionEvent.ACTION_UP) {
            actionUp(view)
            view.performClick()
        }

        return true
    }

    private fun actionMove(view: View, event: MotionEvent): Boolean {
        val deltaX = baseX - event.rawX
        val i = currentPage.value ?: 0

        if (i == 0 && deltaX < 0) {
            return false
        }

        if (i == pagesCount - 1 && deltaX > 0) {
            return false
        }

        if (abs(deltaX) >= 1) {
            view.x = view.x - deltaX
            baseX = event.rawX

            if (!isImageLoaded) {
                onSubPageLoad.invoke(i + (deltaX / abs(deltaX)).toInt())
                isImageLoaded = true
            }
        }

        return true
    }

    private fun actionUp(view: View) {
        baseX = 0f
        isImageLoaded = false
        val screenWidth = (view.parent as View).width

        if (view.x < 0 - screenWidth / 6) {
            moveByX(view, -screenWidth, onSwipeLeft)
        } else if (view.x < screenWidth / 6) {
            moveByX(view, 0) {}
        } else {
            moveByX(view, screenWidth, onSwipeRight)
        }
    }

    private fun moveByX(view: View, value: Int, onEnd: () -> Unit) {
        ObjectAnimator.ofFloat(view, "translationX", value * 1f).apply {
            duration = 500
            start()
        }.doOnEnd { onEnd.invoke() }
    }

}