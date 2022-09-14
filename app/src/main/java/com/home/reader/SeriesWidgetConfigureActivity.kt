package com.home.reader

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.home.reader.component.adapter.SeriesWidgetAdapter
import com.home.reader.databinding.SeriesWidgetConfigureBinding
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.utils.Constants.Sizes.PREVIEW_COVER_WIDTH_IN_DP
import com.home.reader.utils.Constants.Sizes.PREVIEW_GATTER_WIDTH_ID_DP
import com.home.reader.utils.coversPath
import com.home.reader.utils.seriesDao
import com.home.reader.utils.widthInDp
import kotlinx.coroutines.launch
import java.io.File

/**
 * The configuration screen for the [SeriesWidget] AppWidget.
 */
class SeriesWidgetConfigureActivity : AppCompatActivity() {

    private val seriesData = MutableLiveData<MutableList<SeriesWithIssues>>()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var binding: SeriesWidgetConfigureBinding


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = SeriesWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spanCount =
            (widthInDp() / (PREVIEW_COVER_WIDTH_IN_DP + PREVIEW_GATTER_WIDTH_ID_DP)).toInt()

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        seriesData.observe(this) {
            binding.seriesView.layoutManager = GridLayoutManager(this, spanCount)
            binding.seriesView.adapter = SeriesWidgetAdapter(it, this, appWidgetId, R.layout.series_widget)
        }

        updateSeries()

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

    }

    private fun updateSeries() {
        lifecycleScope.launch {
            seriesData.value = seriesDao().getAll()
                .map { (series, issues) -> SeriesWithIssues(series, issues) }
                .toMutableList()
        }
    }

}

const val PREFS_NAME = "com.home.reader.SeriesWidget"
private const val PREF_PREFIX_KEY = "appwidget_"
private const val SERIES_ID_KEY = "series_id_"

internal fun saveCoverIdPref(context: Context, appWidgetId: Int, id: Long) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putLong(PREF_PREFIX_KEY + appWidgetId, id)
    prefs.apply()
}

internal fun saveSeriesIdPref(context: Context, appWidgetId: Int, id: Long) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putLong(PREF_PREFIX_KEY + SERIES_ID_KEY + appWidgetId, id)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadCoverIdPref(context: Context, appWidgetId: Int): Long {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0L)
}

internal fun loadSeriesIdPref(context: Context, appWidgetId: Int): Long {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getLong(PREF_PREFIX_KEY + SERIES_ID_KEY + appWidgetId, 0L)
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}

internal fun getCover(context: Context, id: Long?, width: Int, height: Int): Bitmap? {
    val basePath = "${context.filesDir}/${context.packageName}"
    val dir = File("$basePath/${id}")
    if (!dir.exists()) {
        return null
    }

    val coverFile = context.coversPath().resolve("$id.jpg").toFile()

    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.ARGB_8888

    val bitmap = BitmapFactory.decodeFile(coverFile?.path, options)
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
}