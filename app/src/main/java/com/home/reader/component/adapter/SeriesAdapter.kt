package com.home.reader.component.adapter

import android.app.Activity
import android.content.Intent
import android.view.View
import com.home.reader.component.activity.IssuesActivity
import com.home.reader.component.activity.MainActivity
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.utils.Constants.SeriesExtra.SERIES_ID


class SeriesAdapter(
    series: MutableList<SeriesWithIssues>,
    parent: Activity,
) : AbstractSeriesAdapter(series, parent) {

    override fun onSeriesClick(seriesId: Long?): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(it.context, IssuesActivity::class.java).apply {
                putExtra(SERIES_ID, seriesId)
            }

            (parent as MainActivity).openSeries(intent)
        }
    }

}
