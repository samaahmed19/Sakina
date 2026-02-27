package com.sama.sakina.ui.checklist


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import java.util.concurrent.TimeUnit
import java.time.Duration
import java.time.LocalDateTime
import com.sama.sakina.data.local.database.dao.ChecklistDao


@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checklistDao: ChecklistDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            checklistDao.resetAllTasks()
            Result.success()
        } catch (e: Exception) {

            Result.failure()
        }
    }
    companion object {

        fun schedule(context: Context) {

            val now = LocalDateTime.now()
            val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
            val initialDelay = Duration.between(now, midnight).toMillis()

            val workRequest =
                PeriodicWorkRequestBuilder<DailyResetWorker>(
                    1, TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyResetWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}


