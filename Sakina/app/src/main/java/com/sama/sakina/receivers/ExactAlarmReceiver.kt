package com.sama.sakina.receivers

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
import com.sama.sakina.MainActivity
import com.sama.sakina.services.PrayerForegroundService
import com.sama.sakina.utils.PrayerNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.sama.sakina.utils.AlarmScheduler

@AndroidEntryPoint
class ExactAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationHelper: PrayerNotificationHelper
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ALARM_DEBUG", "Receiver fired action = ${intent.action}")



        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            notificationHelper.updateNextPrayerNotification()


            val scheduler = AlarmScheduler(context)
            scheduler.scheduleExactAzkar(6, 0, "morning")
            scheduler.scheduleExactAzkar(16, 0, "evening")

            return
        }

        val type = intent.getStringExtra("ALARM_TYPE") ?: return

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

