package com.home.reader

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.home.reader.component.adapter.SeriesAdapter
import com.home.reader.databinding.SeriesWidgetConfigureBinding
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.utils.Constants
import com.home.reader.utils.coversPath
import com.home.reader.utils.seriesDao
import com.home.reader.utils.widthInDp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * The configuration screen for the [SeriesWidget] AppWidget.
 */
class SeriesWidgetConfigureActivity : AppCompatActivity() {

    private val seriesData = MutableLiveData<MutableList<SeriesWithIssues>>()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText
    private lateinit var binding: SeriesWidgetConfigureBinding

    private var onClickListener = View.OnClickListener {
        val context = this@SeriesWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetText = appWidgetText.text.toString()
        saveTitlePref(context, appWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = SeriesWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appWidgetText = binding.appwidgetText
        binding.addButton.setOnClickListener(onClickListener)

        val spanCount =
            (widthInDp() / (Constants.Sizes.PREVIEW_COVER_WIDTH_IN_DP + Constants.Sizes.PREVIEW_GATTER_WIDTH_ID_DP)).toInt()

        seriesData.observe(this) {
            binding.seriesView.layoutManager = GridLayoutManager(this, spanCount)
            binding.seriesView.adapter = SeriesAdapter(it, this)
        }

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        updateSeries()

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appWidgetText.setText(loadTitlePref(this@SeriesWidgetConfigureActivity, appWidgetId))
    }

    private fun updateSeries() {
        lifecycleScope.launch {
            seriesData.value = seriesDao().getAll()
                .map { (series, issues) -> SeriesWithIssues(series, issues) }
                .toMutableList()
        }
    }

}

private const val PREFS_NAME = "com.home.reader.SeriesWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    return titleValue ?: context.getString(R.string.appwidget_text)
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