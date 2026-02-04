package com.example.sakina.ui.Checklist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.entity.ChecklistEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistDao: ChecklistDao
) : ViewModel() {

    val allTasks = checklistDao.getAllTasks()

    private val _streakDays = mutableStateOf(0)
    val streakDays: State<Int> = _streakDays

    private var lastCompletedDay: Long = -1L

    init {
        viewModelScope.launch {
            allTasks.collect { tasks ->
                updateStreak(tasks)
            }
        }
    }

    private fun updateStreak(tasks: List<ChecklistEntity>) {
        if (tasks.isEmpty()) return

        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
        val allCompleted = tasks.all { it.isCompleted }

        if (allCompleted && lastCompletedDay != today) {
            _streakDays.value += 1
            lastCompletedDay = today
        }

        if (!allCompleted && lastCompletedDay == today) {
            _streakDays.value -= 1
            lastCompletedDay = -1L
        }
    }

    fun addNewTask(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            checklistDao.addTask(ChecklistEntity(taskName = name))
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
