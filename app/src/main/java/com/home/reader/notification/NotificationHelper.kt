package com.home.reader.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.home.reader.R

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val channelId = "work_progress_channel"
    private val notificationId = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Work Progress",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows progress of background work"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showProgressNotification(max: Int, current: Int) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Background Work")
            .setContentText("Check for updated")
            .setSmallIcon(R.drawable.plus_circle_outline)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(max, current, false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun completeNotification() {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Work Complete")
            .setContentText("Background work finished successfully")
            .setSmallIcon(R.drawable.plus_circle_outline)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)

        Handler(Looper.getMainLooper()).postDelayed({
            notificationManager.cancel(notificationId)
        }, 2000)
    }
}