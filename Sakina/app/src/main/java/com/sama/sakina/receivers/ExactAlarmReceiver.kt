package com.sama.sakina.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sama.sakina.services.PrayerForegroundService
import com.sama.sakina.utils.PrayerNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.sama.sakina.R
import com.sama.sakina.MainActivity


@AndroidEntryPoint
class ExactAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: PrayerNotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("ALARM_TYPE") ?: return

        when (type) {
            "AZKAR" -> {
                val catId = intent.getStringExtra("CAT_ID") ?: "morning"
                val title = if (catId == "morning") "أذكار الصباح" else "أذكار المساء"
                val message = "هل قرأت أذكارك؟ ابدأ الآن."
                showNotification(context, title, message, catId, "AZKAR_CHANNEL")
            }
            "PRAYER" -> {
                val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "الصلاة"
                showNotification(context, "حان الآن موعد صلاة $prayerName", "﴿إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾", "prayer", "PRAYER_ALERT_CHANNEL")

                notificationHelper.updateNextPrayerNotification()
            }
            "DISMISS_SERVICE" -> {
                val serviceIntent = Intent(context, PrayerForegroundService::class.java)
                context.stopService(serviceIntent)
            }
        }
    }

    private fun showNotification(context: Context, title: String, message: String, catId: String, channelId: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تنبيهات سكينة", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val openIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sakina://azkar/$catId"), context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, catId.hashCode(), openIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(catId.hashCode(), notification)
    }
}