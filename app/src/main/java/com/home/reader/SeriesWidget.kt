package com.home.reader

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [SeriesWidgetConfigureActivity]
 */

class SeriesWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {}

    override fun onReceive(context: Context, intent: Intent) {}

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

