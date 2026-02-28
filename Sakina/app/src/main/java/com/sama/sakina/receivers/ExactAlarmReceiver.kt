package com.sama.sakina.receivers

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sama.sakina.R
//import com.sama.sakina.di.AlarmReceiverEntryPoint
//import com.sama.sakina.utils.AlarmScheduler
//import com.sama.sakina.utils.PrayerNotificationHelper
import dagger.hilt.android.EntryPointAccessors

/*class ExactAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        if (intent == null) {
            pendingResult.finish()
            return
        }
        try {
            Log.d("ALARM_DEBUG", "Receiver fired action = ${intent.action}")

            val app = context.applicationContext as? Application ?: run {
                Log.e("ALARM_DEBUG", "Application context is not Application")
                pendingResult.finish()
                return
            }
            val entryPoint = EntryPointAccessors.fromApplication(app, AlarmReceiverEntryPoint::class.java)
            val notificationHelper: PrayerNotificationHelper = entryPoint.prayerNotificationHelper()
            val alarmScheduler: AlarmScheduler = entryPoint.alarmScheduler()

            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                notificationHelper.updateNextPrayerNotification()
                alarmScheduler.scheduleExactAzkar(6, 0, "morning")
                alarmScheduler.scheduleExactAzkar(16, 0, "evening")
                pendingResult.finish()
                return
            }

            val type = intent.getStringExtra("ALARM_TYPE") ?: run {
                pendingResult.finish()
                return
            }

            when (type) {
                "PRAYER" -> {
                    val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "الصلاة"
                    showNotification(
                        context,
                        "حان الآن موعد صلاة $prayerName",
                        "﴿إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوقُوتًا﴾",
                        "prayer",
                        "PRAYER_ALERT_CHANNEL"
                    )
                    notificationHelper.updateNextPrayerNotification()
                }

                "AZKAR" -> {
                    val catId = intent.getStringExtra("CAT_ID") ?: "morning"
                    val title =
                        if (catId == "morning") "أذكار الصباح"
                        else "أذكار المساء"
                    showNotification(
                        context,
                        title,
                        "هل قرأت أذكارك؟ ابدأ الآن.",
                        catId,
                        "AZKAR_CHANNEL"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ALARM_DEBUG", "Error in onReceive", e)
        } finally {
            pendingResult.finish()
        }
    }
}

private fun showNotification(context: Context, title: String, message: String, catId: String, channelId: String) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "تنبيهات الأذان والأذكار", NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
            enableLights(true)
            setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }

    val openIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sakina://azkar/$catId"))


    val requestCode = when(catId) {
        "morning" -> 100
        "evening" -> 101
        "prayer" -> 102
        else -> 103
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        openIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.appicon)
        .setContentTitle(title)
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .build()


    val finalNotificationId = if (catId == "prayer") 50 else requestCode

    manager.notify(finalNotificationId, notification)
}

*/