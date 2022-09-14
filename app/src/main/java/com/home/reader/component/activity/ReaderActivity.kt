package com.home.reader.component.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.home.reader.component.event.reader.CenterZone
import com.home.reader.component.event.reader.DeadZone
import com.home.reader.component.event.reader.LeftZone
import com.home.reader.component.event.reader.RightZone
import com.home.reader.databinding.ActivityReaderBinding
import com.home.reader.persistence.entity.Issue
import com.home.reader.utils.Constants.SeriesExtra.ISSUE_DIR
import com.home.reader.utils.Constants.SeriesExtra.SERIES_ID
import com.home.reader.utils.issueDao
import kotlinx.coroutines.launch
import java.io.File

class ReaderActivity : AppCompatActivity() {

    private var currentIssue = MutableLiveData<Issue>()
    private lateinit var pages: List<File>
    private lateinit var binding: ActivityReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deadZoneHandler
            .setNext(centerZoneHandler)
            ?.setNext(leftZoneHandler)
            ?.setNext(rightZoneHandler)

        val seriesId = intent.getLongExtra(SERIES_ID, -1)
        val id = intent.getLongExtra(ISSUE_DIR, -1)
        if (id == -1L && seriesId == -1L) {
            return
        }

        if (seriesId != -1L) {
            lifecycleScope.launch { initSeriesReaderMode(seriesId) }
            return
        }

        initPages(id)

        currentIssue.observe(this, pageUpdateObserver)

        with(binding.readingProgress) {
            max = pages.size - 1
            progress = currentIssue.value?.currentPage ?: 0
            setOnSeekBarChangeListener(progressBarUpdater)
        }

        lifecycleScope.launch {
            currentIssue.value = issueDao().findById(id)
            binding.currentPage.setOnTouchListener(touchPageListener)
        }
    }

    private suspend fun initSeriesReaderMode(seriesId: Long) {
        val id = issueDao().findLastIssueBySeriesId(seriesId)?.id!!
        initPages(id)

        currentIssue.observe(this, pageUpdateObserver)

        with(binding.readingProgress) {
            max = pages.size - 1
            progress = currentIssue.value?.currentPage ?: 0
            setOnSeekBarChangeListener(progressBarUpdater)
        }

        currentIssue.value = issueDao().findById(id)
        binding.currentPage.setOnTouchListener(touchPageListener)
    }

    private fun initPages(issueId: Long) {
        pages = File("$filesDir/$packageName/$issueId")
            .listFiles()?.let {
                it.sortedBy { file -> file.name }
            } ?: ArrayList()
    }

    private val touchPageListener = { v: View, event: MotionEvent ->
        deadZoneHandler.handle(v, event.x, event.y)
        v.performClick()
    }

    private val pageUpdateObserver = Observer { issue: Issue ->
        val i = issue.currentPage

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val bitmap = BitmapFactory.decodeFile(pages[i].path, options)
        binding.currentPage.setImageBitmap(bitmap)
        binding.readingProgress.progress = i

        lifecycleScope.launch {
            issueDao().update(issue)
        }
    }

    private val progressBarUpdater = object : OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            val value = i * (seekBar.width + 2 * seekBar.thumbOffset) / seekBar.max
            binding.currentPageNumber.text = i.toString()
            binding.currentPageNumber.x = seekBar.x + value + seekBar.thumbOffset / 2

            currentIssue.value?.apply {
                currentPage = i
                currentIssue.value = this
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val deadZoneHandler = DeadZone(0) {}

    private val leftZoneHandler = LeftZone(1) {
        binding.readingProgressContainer.visibility = INVISIBLE

        currentIssue.value?.apply {
            if (currentPage > 0) {
                currentPage--
                currentIssue.value = this
            }
        }
    }

    private val rightZoneHandler = RightZone(1) {
        binding.readingProgressContainer.visibility = INVISIBLE

        currentIssue.value?.apply {
            if (currentPage < pages.size - 1) {
                currentPage++
                currentIssue.value = this
            }
        }
    }

    private val centerZoneHandler = CenterZone(-1) {
        if (binding.readingProgressContainer.visibility == VISIBLE) {
            binding.readingProgressContainer.visibility = INVISIBLE
        } else {
            binding.readingProgressContainer.visibility = VISIBLE
        }

    }
}