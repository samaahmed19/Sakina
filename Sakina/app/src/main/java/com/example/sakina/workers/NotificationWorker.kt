package com.example.sakina.workers
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.sakina.MainActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar
import android.annotation.SuppressLint
import com.example.sakina.R

class AzkarWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
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
        return Result.success()
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String, catId: String) {
        val channelId = "AZKAR_NOTIFICATIONS"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Azkar", NotificationManager.IMPORTANCE_HIGH)
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val deepLinkUri = "sakan://azkar/$catId".toUri()

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