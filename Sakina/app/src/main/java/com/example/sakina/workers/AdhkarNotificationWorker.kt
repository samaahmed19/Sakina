package com.example.sakina.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sakina.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MorningAdhkarWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showNotification(
            context = applicationContext,
            channelId = CHANNEL_MORNING,
            channelName = "أذكار الصباح",
            title = "هل قرأت أذكار الصباح اليوم؟",
            body = "ابدأ",
            targetRoute = "azkar_details/morning"
        )
        return Result.success()
    }

    companion object {
        const val CHANNEL_MORNING = "azkar_morning"
    }
}

@HiltWorker
class EveningAdhkarWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showNotification(
            context = applicationContext,
            channelId = CHANNEL_EVENING,
            channelName = "أذكار المساء",
            title = "هل قرأت أذكار المساء اليوم؟",
            body = "ابدأ",
            targetRoute = "azkar_details/evening"
        )
        return Result.success()
    }

    companion object {
        const val CHANNEL_EVENING = "azkar_evening"
    }
}

@HiltWorker
class DuaOfDayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("sakina_prefs", Context.MODE_PRIVATE)
        val duaText = prefs.getString("last_dua_text", null)
            ?: "اللَّهُمْ إِنِّي أَسْأَلُكَ عِلْماً نَافِعاً ، وَأَعُوذُ بِكَ مِنْ عِلْمٍ لا يَنفَع"

        showNotification(
            context = applicationContext,
            channelId = CHANNEL_DUA,
            channelName = "دعاء اليوم",
            title = "دعاء اليوم",
            body = duaText.take(80) + (if (duaText.length > 80) "..." else ""),
            targetRoute = "home"
        )
        return Result.success()
    }

    companion object {
        const val CHANNEL_DUA = "dua_of_day"
    }
}

private fun showNotification(
    context: Context,
    channelId: String,
    channelName: String,
    title: String,
    body: String,
    targetRoute: String
) {
    createNotificationChannel(context, channelId, channelName)

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("notification_target", targetRoute)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        channelId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    try {
        NotificationManagerCompat.from(context).notify(channelId.hashCode(), notification)
    } catch (_: SecurityException) {}
}

private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "إشعارات $channelName"
            enableVibration(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
