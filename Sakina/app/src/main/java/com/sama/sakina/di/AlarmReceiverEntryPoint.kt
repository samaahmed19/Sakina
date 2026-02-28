package com.sama.sakina.di

import com.sama.sakina.utils.AlarmScheduler
import com.sama.sakina.utils.PrayerNotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for components that are created by the system (e.g. ExactAlarmReceiver
 * when an alarm fires) and therefore do not receive Hilt field injection.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {
    fun prayerNotificationHelper(): PrayerNotificationHelper
    fun alarmScheduler(): AlarmScheduler
}
