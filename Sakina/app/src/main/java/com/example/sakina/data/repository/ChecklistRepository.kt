package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.dao.StreakDao
import com.example.sakina.data.local.database.entity.ChecklistEntity
import java.util.*
import javax.inject.Inject
import com.example.sakina.data.local.database.entity.StreakEntity

class ChecklistRepository @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val streakDao: StreakDao
) {
    val allTasks = checklistDao.getAllTasks()
    val streak = streakDao.getStreak()

    suspend fun toggleTask(task: ChecklistEntity) {
        val updatedTask = task.copy(
            isCompleted = !task.isCompleted
        )
        checklistDao.updateTask(updatedTask)
        updateStreakLogic()
    }

    private suspend fun updateStreakLogic() {
        val tasks = checklistDao.getTasksOnce()
        val allDone = tasks.isNotEmpty() && tasks.all { it.isCompleted }
        val currentStreak = streakDao.getStreakOnce() ?: StreakEntity()
        val today = getStartOfDay()

        if (allDone && currentStreak.lastCheckDate < today) {
            streakDao.updateStreak(
                currentStreak.copy(
                    count = currentStreak.count + 1,
                    lastCheckDate = today
                )
            )
        }
    }

    suspend fun performDailyReset() {
        val currentStreak = streakDao.getStreakOnce() ?: StreakEntity()
        val today = getStartOfDay()
        val yesterday = today - 86400000

        if (currentStreak.lastCheckDate < yesterday && currentStreak.lastCheckDate != 0L) {
            streakDao.updateStreak(currentStreak.copy(count = 0))
        }
        checklistDao.resetAllTasks()
    }

    private fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}