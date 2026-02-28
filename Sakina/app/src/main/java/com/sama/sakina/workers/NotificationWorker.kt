package com.sama.sakina.workers
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sama.sakina.MainActivity
import androidx.core.app.NotificationCompat
import android.content.BroadcastReceiver
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sama.sakina.R
import com.sama.sakina.services.PrayerForegroundService
import com.sama.sakina.utils.PrayerNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject


class AzkarWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            val categoryId: String
            val title = "هل قرأت أذكارك اليوم؟"
            val message: String

            if (hour < 12) {
                categoryId = "morning"
                message = "ابدأ أذكار الصباح الآن."
            } else {
                categoryId = "evening"
                message = "ابدأ أذكار المساء الآن."
            }

            showNotification(title, message, categoryId)
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("AzkarWorker", "doWork failed", e)
            Result.failure()
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String, catId: String) {
        val channelId = "AZKAR_NOTIFICATIONS"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Azkar", NotificationManager.IMPORTANCE_HIGH)
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val deepLinkUri = "sakina://azkar/$catId".toUri()

        val intent = Intent(
            Intent.ACTION_VIEW,
            deepLinkUri,
            applicationContext,
            MainActivity::class.java
        )

        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        NotificationManagerCompat.from(applicationContext).notify(catId.hashCode(), notification)
    }
}

class PrayerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        return try {
            val prayerName = inputData.getString("PRAYER_NAME") ?: "الصلاة"
            val isOngoing = inputData.getBoolean("IS_ONGOING", false)

            val channelId = "PRAYER_NOTIFICATIONS"

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle("حان الآن موعد صلاة $prayerName")
                .setContentText("﴿إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾")
                .setOngoing(isOngoing)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(!isOngoing)
                .build()

            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1001, notification)

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("PrayerWorker", "doWork failed", e)
            Result.failure()
        }
    }
}
