package com.home.reader.component.adapter

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.view.View.OnClickListener
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.home.reader.R
import com.home.reader.component.activity.ReaderActivity
import com.home.reader.getCover
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.utils.Constants.SeriesExtra.SERIES_ID

class SeriesWidgetAdapter(
    series: MutableList<SeriesWithIssues>,
    parent: Activity,
    private val appWidgetId: Int,
    private val widgetLayoutId: Int
) : AbstractSeriesAdapter(series, parent) {

    override fun onSeriesClick(seriesId: Long?): OnClickListener {
        return OnClickListener {
            val swi = series.find { it.series.id == seriesId }!!
            val coverId = swi.issues.first().id!!

            val widgetManager = AppWidgetManager.getInstance(parent)
            val views = RemoteViews(parent.packageName, widgetLayoutId)

            views.setBitmap(R.id.seriesCover, "setImageBitmap", getCover(parent, coverId, 110, 177))
            views.setTextViewText(R.id.seriesName, swi.series.name ?: "")

            val intent = Intent(parent, ReaderActivity::class.java).apply {
                putExtra(SERIES_ID, seriesId)
            }

            val pendingIntent = PendingIntent.getActivity(parent, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.seriesCover, pendingIntent)

            widgetManager.updateAppWidget(appWidgetId, views)

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            parent.setResult(AppCompatActivity.RESULT_OK, resultValue)
            parent.finish()
        }
    }
}