package com.sama.sakina.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.sama.sakina.MainActivity
import com.sama.sakina.R
import com.sama.sakina.receivers.ExactAlarmReceiver
import java.util.Locale

class PrayerForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nextPrayerName = intent?.getStringExtra("PRAYER_NAME") ?: "..."
        val nextPrayerTimeMillis = intent?.getLongExtra("PRAYER_TIME", System.currentTimeMillis())
            ?: System.currentTimeMillis()

        try {
            val notification = createCustomNotification(nextPrayerName, nextPrayerTimeMillis)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(1, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return START_STICKY
    }

    private fun createCustomNotification(prayerName: String, timeMillis: Long): android.app.Notification {
        val channelId = "PRAYER_ONGOING_CHANNEL"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "الإشعار الثابت للصلوات", NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            manager.createNotificationChannel(channel)
        }

        val remoteViews = RemoteViews(packageName, R.layout.custom_prayer_notification)

        remoteViews.setTextViewText(R.id.tv_prayer_name, "التالي $prayerName")

        val sdf = java.text.SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val formattedTime = sdf.format(java.util.Date(timeMillis))
        remoteViews.setTextViewText(R.id.tv_prayer_time_formatted, formattedTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            remoteViews.setChronometerCountDown(R.id.chronometer, true)
        }

        val baseTime = SystemClock.elapsedRealtime() + (timeMillis - System.currentTimeMillis())
        remoteViews.setChronometer(R.id.chronometer, baseTime, null, true)

        // الروابط
        val azkarIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sakina://azkar_list"), this, MainActivity::class.java)
        val azkarPending = PendingIntent.getActivity(this, 10, azkarIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.btn_azkar, azkarPending)

        val duaIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sakina://dua_list"), this, MainActivity::class.java)
        val duaPending = PendingIntent.getActivity(this, 11, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.btn_dua, duaPending)

        val closeIntent = Intent(this, ExactAlarmReceiver::class.java).apply {
            action = "DISMISS_SERVICE_ACTION"
            putExtra("ALARM_TYPE", "DISMISS_SERVICE")
        }
        val closePending = PendingIntent.getBroadcast(this, 99, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.btn_close_notification, closePending)

        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPending = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.appicon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setContentIntent(mainPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}