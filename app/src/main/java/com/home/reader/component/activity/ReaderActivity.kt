package com.home.reader.component.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.LruCache
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.home.reader.component.event.reader.OnLeafListener
import com.home.reader.databinding.ActivityReaderBinding
import com.home.reader.persistence.entity.Issue
import com.home.reader.utils.Constants.SeriesExtra.ISSUE_DIR
import com.home.reader.utils.Constants.SeriesExtra.SERIES_ID
import com.home.reader.utils.issueDao
import kotlinx.coroutines.launch
import java.io.File

class ReaderActivity : AppCompatActivity() {

    private lateinit var currentIssue: Issue
    private var pageNumber = MutableLiveData<Int>()
    private lateinit var pages: List<File>
    private lateinit var binding: ActivityReaderBinding
    private val cache = object : LruCache<Int, Bitmap>(5) {}

    companion object {
        private val IMAGE_OPTIONS = BitmapFactory.Options().apply {
            this.inPreferredConfig = Bitmap.Config.ARGB_8888
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        pageNumber.observe(this, pageUpdateObserver)

        with(binding.readingProgress) {
            max = pages.size - 1
            progress = pageNumber.value ?: 0
            setOnSeekBarChangeListener(progressBarUpdater)
        }

        lifecycleScope.launch {
            currentIssue = issueDao().findById(id)!!
            pageNumber.value = currentIssue.currentPage
            binding.currentPage.setOnTouchListener(createOnLeafListener())
        }

        configureSystemUI()
    }

    private suspend fun initSeriesReaderMode(seriesId: Long) {
        val id = issueDao().findLastIssueBySeriesId(seriesId)?.id!!
        initPages(id)

        pageNumber.observe(this, pageUpdateObserver)

        with(binding.readingProgress) {
            max = pages.size - 1
            progress = pageNumber.value ?: 0
            setOnSeekBarChangeListener(progressBarUpdater)
        }

        currentIssue = issueDao().findById(id)!!
        pageNumber.value = currentIssue.currentPage
        binding.currentPage.setOnTouchListener(createOnLeafListener())
    }

    private fun initPages(issueId: Long) {
        pages = File("$filesDir/$packageName/$issueId")
            .listFiles()?.let {
                it.sortedBy { file -> file.name }
            } ?: ArrayList()
    }

    private fun configureSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private val pageUpdateObserver = Observer { page: Int ->
        val bitmap = loadPage(page)
        binding.currentPage.setImageBitmap(bitmap)
        binding.currentPage.x = 0F
        binding.readingProgress.progress = page

        currentIssue.currentPage = page

        lifecycleScope.launch {
            issueDao().update(currentIssue)
        }
    }

    private fun createOnLeafListener() = OnLeafListener(
        currentPage = pageNumber,
        pagesCount = pages.size,
        onSubPageLoad = {
            val page = loadPage(it)
            binding.shufflePage.setImageBitmap(page)
        },
        onSwipeLeft = {
            pageNumber.value?.apply {
                if (this < pages.size - 1) {
                    pageNumber.value = this + 1
                }
            }
        },
        onSwipeRight = {
            pageNumber.value?.apply {
                if (this > 0) {
                    pageNumber.value = this - 1
                }
            }
        }
    )

    private fun loadPage(pageNumber: Int): Bitmap {
        val bitmap = cache.get(pageNumber)
        if (bitmap != null) {
            return bitmap
        }

        val page = BitmapFactory.decodeFile(pages[pageNumber].path, IMAGE_OPTIONS)
        cache.put(pageNumber, page)

        return page
    }

    private val progressBarUpdater = object : OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            val value = i * (seekBar.width + 2 * seekBar.thumbOffset) / seekBar.max
            binding.currentPageNumber.text = i.toString()
            binding.currentPageNumber.x = seekBar.x + value + seekBar.thumbOffset / 2

            pageNumber.value?.apply {
                pageNumber.value = i
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

}