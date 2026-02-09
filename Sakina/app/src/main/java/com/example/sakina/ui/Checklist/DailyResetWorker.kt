package com.example.sakina.ui.checklist

import com.example.sakina.data.local.database.dao.ChecklistDao
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checklistDao: ChecklistDao // تأكدي إنها private val
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // ضيفي السطر ده عشان تشوفي الأيرور فين في الـ Logcat
            println("DailyResetWorker: Starting Reset...")

            checklistDao.resetAllTasks()

            println("DailyResetWorker: Reset Successful!")
            Result.success()
        } catch (e: Exception) {
            println("DailyResetWorker: Error - ${e.message}")
            Result.failure()
        }
    }
}


