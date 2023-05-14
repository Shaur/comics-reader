package com.home.reader.component.event.reader

import android.animation.ObjectAnimator
import android.util.Log
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
    private var startTime = 0L
    private var currentLoadedPage = -1

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            baseX = event.rawX
            startTime = System.currentTimeMillis()
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            if (!actionMove(view, event)) return false
        }

        if (event.action == MotionEvent.ACTION_UP) {
            actionUp(view, event.x)
            view.performClick()
        }

        return true
    }

    private fun actionMove(view: View, event: MotionEvent): Boolean {
        val deltaX = baseX - event.rawX
        val i = currentPage.value ?: 0

        if (i == 0 && baseX < event.rawX) {
            return false
        }

        if (i == pagesCount - 1 && baseX > event.rawX) {
            return false
        }

        if (abs(deltaX) >= 1) {
            view.x = view.x - deltaX
            baseX = event.rawX

            val pageNumber = i + (view.x / abs(view.x)).toInt()
            if (currentLoadedPage != pageNumber) {
                onSubPageLoad.invoke(i + (deltaX / abs(deltaX)).toInt())
                currentLoadedPage = pageNumber
            }
        }

        return true
    }

    private fun actionUp(view: View, eventX: Float) {
        val vector = baseX - eventX
        baseX = 0f
        currentLoadedPage = -1
        val screenWidth = (view.parent as View).width

        Log.i("", "Vector is $vector")
        if (abs(vector) > 100) {
            return if (vector < 0) {
                moveByX(view, -screenWidth, onSwipeLeft)
            } else {
                moveByX(view, screenWidth, onSwipeRight)
            }
        } else {
            moveByX(view, 0) {}
        }
    }

    private fun moveByX(view: View, value: Int, onEnd: () -> Unit) {
        ObjectAnimator.ofFloat(view, "translationX", value * 1f).apply {
            duration = 500
            start()
        }.doOnEnd { onEnd.invoke() }
    }

}