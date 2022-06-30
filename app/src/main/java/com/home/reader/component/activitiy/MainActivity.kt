package com.home.reader.component.activitiy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.home.reader.async.ImportComicsWorker
import com.home.reader.async.ImportComicsWorker.Companion.IMPORT_WORKER_RESULT_KEY
import com.home.reader.async.ImportComicsWorker.Companion.IMPORT_WORKER_SERIES_ID_KEY
import com.home.reader.async.ImportComicsWorker.Companion.IMPORT_WORKER_URI_KEY
import com.home.reader.component.adapter.SeriesAdapter
import com.home.reader.databinding.ActivityMainBinding
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.utils.*
import com.home.reader.utils.Constants.COMICS_MIME_TYPES
import com.home.reader.utils.Constants.Sizes.PREVIEW_COVER_WIDTH_IN_DP
import com.home.reader.utils.Constants.Sizes.PREVIEW_GATTER_WIDTH_ID_DP
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val seriesData = MutableLiveData<MutableList<SeriesWithIssues>>()
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var binding: ActivityMainBinding
    private lateinit var workerManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNecessaryFolders()

        seriesRepository = SeriesRepository(seriesDao(), issueDao())
        workerManager = WorkManager.getInstance(applicationContext)

        val spanCount =
            (widthInDp() / (PREVIEW_COVER_WIDTH_IN_DP + PREVIEW_GATTER_WIDTH_ID_DP)).toInt()

        binding.seriesView.layoutManager = GridLayoutManager(this, spanCount)

        seriesData.observe(this) {
            binding.seriesView.adapter = SeriesAdapter(it, this)
        }

        updateSeries()

        binding.importBtn.setOnClickListener {
            checkReadStoragePermission()

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .putExtra(Intent.EXTRA_MIME_TYPES, COMICS_MIME_TYPES)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            chooseFileLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }
    }

    private fun updateSeries() {
        lifecycleScope.launch {
            seriesData.value = seriesDao().getAll()
                .map { (series, issues) -> SeriesWithIssues(series, issues) }
                .toMutableList()
        }
    }

    private fun insertSeries(seriesId: Long) {
        lifecycleScope.launch {
            val series = seriesDao().getSeriesById(seriesId)
            (binding.seriesView.adapter as SeriesAdapter).addItem(series)
        }
    }

    private fun createNecessaryFolders() {
        dataDir().mkdirs()
        coversDir().mkdirs()
    }

    fun openSeries(intent: Intent) {
        openSeriesResult.launch(intent)
    }

    private val openSeriesResult = registerForActivityResult(StartActivityForResult()) {
        updateSeries()
    }

    private var chooseFileLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }

        val clipData = result.data?.clipData
        val selectedFiles = if (clipData != null) {
            (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }
        } else {
            listOf(result.data?.data)
        }

        for (uri in selectedFiles) {
            val data = Data.Builder()
                .putString(IMPORT_WORKER_URI_KEY, uri.toString())
                .build()

            val importComicsWorkerRequest = OneTimeWorkRequestBuilder<ImportComicsWorker>()
                .setInputData(data)
                .build()

            workerManager.getWorkInfoByIdLiveData(importComicsWorkerRequest.id)
                .observe(this, importFileObserver)

            workerManager.enqueue(importComicsWorkerRequest)
        }
    }

    private val importFileObserver = Observer<WorkInfo> {
        val importIssueName = it.outputData.getString(IMPORT_WORKER_RESULT_KEY)
        val seriesId = it.outputData.getLong(IMPORT_WORKER_SERIES_ID_KEY, -1L)
        if (importIssueName != null) {
            Toast.makeText(this, importIssueName, Toast.LENGTH_SHORT).show()
        }

        if (seriesId != -1L) {
            insertSeries(seriesId)
        }
    }

}