package com.sama.sakina.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sama.sakina.data.repository.PrayerSettingsRepository
import com.sama.sakina.data.repository.UserRepository
//import com.sama.sakina.services.PrayerForegroundService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/*@Singleton
class PrayerNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val settingsRepository: PrayerSettingsRepository,
    private val calculator: PrayerTimesCalculator,
    private val alarmScheduler: AlarmScheduler,
    private val appScope: CoroutineScope,

) {
    fun updateNextPrayerNotification() {
        appScope.launch {
            try {
                val user = userRepository.getUserOnce()
                val settings = settingsRepository.getOnce()

                val locationStr = user?.location ?: return@launch
                val parts = locationStr.split(",")
                if (parts.size != 2) return@launch
                val lat = parts[0].trim().toDoubleOrNull() ?: return@launch
                val lng = parts[1].trim().toDoubleOrNull() ?: return@launch

                val now = Calendar.getInstance()

                var fardTimes = calculator.calculateFardTimes(lat, lng, now, settings).asMap()
                var nextPrayer = fardTimes.entries.firstOrNull { it.value > now.timeInMillis }

                if (nextPrayer == null) {
                    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                    fardTimes = calculator.calculateFardTimes(lat, lng, tomorrow, settings).asMap()
                    nextPrayer = fardTimes.entries.firstOrNull()
                }

                nextPrayer?.let { (key, timeInMillis) ->
                    val prayerName = getPrayerNameAr(key.key)

                    val serviceIntent = Intent(context, PrayerForegroundService::class.java).apply {
                        putExtra("PRAYER_NAME", prayerName)
                        putExtra("PRAYER_TIME", timeInMillis)
                    }
                    try {
                        context.stopService(Intent(context, PrayerForegroundService::class.java))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                    } catch (e: Exception) {
                        Log.e("PrayerNotif", "Failed to start service", e)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmScheduler.scheduleExactPrayer(timeInMillis, prayerName)

                        }
                    } else {
                        alarmScheduler.scheduleExactPrayer(timeInMillis, prayerName)
                    }
                }
            } catch (e: Exception) {
                Log.e("PrayerNotif", "Error updating notification", e)
            }
        }
    }

    private fun getPrayerNameAr(key: String): String {
        return when(key) {
            "PRAYER_FAJR" -> "الفجر"
            "PRAYER_DHUHR" -> "الظهر"
            "PRAYER_ASR" -> "العصر"
            "PRAYER_MAGHRIB" -> "المغرب"
            "PRAYER_ISHA" -> "العشاء"
            else -> "الصلاة"
        }
    }
}*/