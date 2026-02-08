package com.example.sakina.ui.Checklist

import ChecklistDao
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val checklistDao: ChecklistDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        checklistDao.resetAllTasks()
        return Result.success()
    }
}



