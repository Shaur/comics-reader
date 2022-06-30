package com.home.reader.component.activitiy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.home.reader.component.adapter.IssuesAdapter
import com.home.reader.databinding.ActivityIssuesBinding
import com.home.reader.utils.Constants.SeriesExtra.SERIES_ID
import com.home.reader.utils.seriesDao
import com.home.reader.utils.widthInDp
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class IssuesActivity : AppCompatActivity() {

    private var seriesId by Delegates.notNull<Long>()
    private lateinit var binding: ActivityIssuesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val spanCount = (widthInDp() / (110 + 10)).toInt()
        binding.issuesView.layoutManager = GridLayoutManager(this, spanCount)

        seriesId = intent.getLongExtra(SERIES_ID, -1L)
        update()
    }

    private fun update() {
        lifecycleScope.launch {
            val series = seriesDao().getSeriesById(seriesId)
            supportActionBar?.title = series.series.name

            binding.issuesView.adapter = IssuesAdapter(
                series.series.name,
                series.issues.sortedBy { it.issue }.toMutableList(),
                lifecycleScope,
                this@IssuesActivity
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 50) {
            update()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}