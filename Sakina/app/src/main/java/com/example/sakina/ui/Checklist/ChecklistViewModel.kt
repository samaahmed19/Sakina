package com.example.sakina.ui.Checklist

import ChecklistDao
import StreakDao
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.ChecklistEntity
import com.example.sakina.data.local.database.entity.StreakEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val streakDao: StreakDao
) : ViewModel() {

    /* ---------------- TASKS ---------------- */

    val allTasks = checklistDao.getAllTasks()

    /* ---------------- STREAK STATE ---------------- */

    private val _streakDays = mutableStateOf(0)
    val streakDays: State<Int> = _streakDays

    private var lastCompletedDay: Long = -1L

    init {
        // Load saved streak once
        viewModelScope.launch {
            streakDao.getStreak()?.let { streak ->
                _streakDays.value = streak.streakDays
                lastCompletedDay = streak.lastCompletedDay
            }
        }

        // Observe tasks and update streak
        viewModelScope.launch {
            allTasks.collect { tasks ->
                updateStreak(tasks)
            }
        }
    }

    /* ---------------- STREAK LOGIC ---------------- */

    private fun updateStreak(tasks: List<ChecklistEntity>) {
        if (tasks.isEmpty()) return

        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
        val allCompleted = tasks.all { it.isCompleted }

        if (!allCompleted) return

        when {
            // First time ever
            lastCompletedDay == -1L -> {
                saveStreak(1, today)
            }

            // Already counted today
            lastCompletedDay == today -> Unit

            // Consecutive day
            lastCompletedDay == today - 1 -> {
                saveStreak(_streakDays.value + 1, today)
            }

            // Missed days → reset
            lastCompletedDay < today - 1 -> {
                saveStreak(1, today)
            }
        }
    }

    private fun saveStreak(days: Int, day: Long) {
        _streakDays.value = days
        lastCompletedDay = day

        viewModelScope.launch {
            streakDao.saveStreak(
                StreakEntity(
                    id = 0,
                    streakDays = days,
                    lastCompletedDay = day
                )
            )
        }
    }

    /* ---------------- TASK ACTIONS ---------------- */

    fun addNewTask(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            checklistDao.addTask(
                ChecklistEntity(taskName = name)
            )
        }
    }

    fun toggleTask(task: ChecklistEntity) {
        viewModelScope.launch {
            checklistDao.updateTaskStatus(
                task.copy(isCompleted = !task.isCompleted)
            )
        }
    }

    fun deleteTask(task: ChecklistEntity) {
        viewModelScope.launch {
            checklistDao.deleteTask(task)
        }
    }
}
