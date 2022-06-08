package com.home.reader.component.activitiy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.home.reader.component.adapter.SeriesAdapter
import com.home.reader.component.dto.CbrMeta
import com.home.reader.databinding.ActivityMainBinding
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.utils.*
import com.home.reader.utils.Constants.COMICS_MIME_TYPES
import com.home.reader.utils.Constants.RequestCodes.BACK_CODE
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.StandardCopyOption


class MainActivity : AppCompatActivity() {

    private val seriesData = MutableLiveData<MutableList<SeriesWithIssues>>()
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNecessaryFolders()

        seriesRepository = SeriesRepository(seriesDao(), issueDao())

        val spanCount = (widthInDp() / (110 + 10)).toInt()
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
        }
    }

    private fun createNecessaryFolders() {
        dataDir().mkdirs()
        coversDir().mkdirs()
    }

    private var chooseFileLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == BACK_CODE) {
            updateSeries()
        } else if (result.resultCode == RESULT_OK) {
            val intent = result.data

            val selectedFiles = intent?.clipData ?: return@registerForActivityResult
            val filesCount = selectedFiles.itemCount
            for (i in 0 until filesCount) {
                val uri = selectedFiles.getItemAt(i).uri
                importFile(uri)
            }
        }
    }

    private fun importFile(uri: Uri) {
        val fileName = getFileName(uri) ?: return
        var input = contentResolver.openInputStream(uri) ?: return

        val meta = CbrUtil.getMeta(input, fileName)

        lifecycleScope.launch {
            val issueId = resolveIssueId(meta)

            val issueDir = dataDir().resolve(issueId.toString())

            input = contentResolver.openInputStream(uri) ?: return@launch
            CbrUtil.extract(input, issueDir)

            val cover = coversPath().resolve("$issueId.jpg")
            val firstPage = (issueDir.listFiles() ?: arrayOf()).minByOrNull { it.name }
            firstPage?.let {
                Files.copy(it.toPath(), cover, StandardCopyOption.REPLACE_EXISTING)
            }

            updateSeries()
        }
    }

    private suspend fun resolveIssueId(meta: CbrMeta): Long {
        return with(meta) {
            var series = seriesDao().getSeriesByName(seriesName)
            if (series == null) {
                val seriesId = seriesDao().insert(Series(name = seriesName))
                series = Series(seriesId, seriesName)
            }

            var issue = issueDao().findBySeriesIdAndIssue(series.id!!, number)
            if (issue != null) {
                return@with issue.id!!
            }

            issue = Issue(
                issue = number,
                seriesId = series.id!!,
                pagesCount = pagesCount
            )

            return@with issueDao().insert(issue)
        }
    }

}