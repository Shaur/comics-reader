package com.home.reader.component.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.WorkManager
import com.home.reader.api.ApiProcessor
import com.home.reader.api.dto.Series
import com.home.reader.component.adapter.SeriesAdapter
import com.home.reader.databinding.ActivityMainBinding
import com.home.reader.utils.Constants.Sizes.PREVIEW_COVER_WIDTH_IN_DP
import com.home.reader.utils.Constants.Sizes.PREVIEW_GATTER_WIDTH_ID_DP
import com.home.reader.utils.coversDir
import com.home.reader.utils.dataDir
import com.home.reader.utils.widthInDp
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private val seriesData = MutableLiveData<MutableList<Series>>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var workerManager: WorkManager
    private var spanCount by Delegates.notNull<Int>()
    private val executor: Executor = Executors.newScheduledThreadPool(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNecessaryFolders()

        workerManager = WorkManager.getInstance(applicationContext)

        spanCount = (widthInDp() / (PREVIEW_COVER_WIDTH_IN_DP + PREVIEW_GATTER_WIDTH_ID_DP)).toInt()

        seriesData.observe(this) {
            binding.seriesView.layoutManager = GridLayoutManager(this, spanCount)
            binding.seriesView.adapter = SeriesAdapter(it, this)
        }

        updateSeries()

//        binding.importBtn.setOnClickListener {
//            checkReadStoragePermission()
//
//            val intent = Intent()
//                .setType("*/*")
//                .setAction(Intent.ACTION_GET_CONTENT)
//                .putExtra(Intent.EXTRA_MIME_TYPES, COMICS_MIME_TYPES)
//                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//
//            chooseFileLauncher.launch(Intent.createChooser(intent, "Select a file"))
//        }
    }

    private fun updateSeries() {
        executor.execute {
//            val token = api.login("user", "password")
//            val series = api.getSeries(token.value!!)
//            seriesData.value = series.toMutableList()
//            seriesData.postValue(series.value!!.toMutableList())
        }

//        lifecycleScope.launch {
//            val token = api.login("user", "password")
//            val series = api.getSeries(token.getOrThrow()).map { SeriesWithIssues(it, listOf()) }
//            seriesData.value = series.toMutableList()
//                seriesDao().getAll()
//                .map { (series, issues) -> SeriesWithIssues(series, issues) }
//                .toMutableList()
//        }
    }

    private fun createNecessaryFolders() {
        dataDir().mkdirs()
        coversDir().mkdirs()
    }

    fun openSeries(intent: Intent) {
        openSeriesResult.launch(intent)
    }

    private val openSeriesResult = registerForActivityResult(StartActivityForResult()) {
        this@MainActivity.recreate()
    }

}