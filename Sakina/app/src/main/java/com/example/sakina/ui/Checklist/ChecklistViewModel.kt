package com.example.sakina.ui.checklist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.dao.StreakDao
import com.example.sakina.data.local.database.entity.ChecklistEntity
import com.example.sakina.data.local.database.entity.StreakEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val streakDao: StreakDao
) : ViewModel() {

    val tasks: StateFlow<List<ChecklistEntity>> = checklistDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streak: StateFlow<StreakEntity?> = streakDao.getStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _streakDaysCount = mutableIntStateOf(0)
    val streakDaysCount: State<Int> = _streakDaysCount

    init {
        viewModelScope.launch {
            streakDao.getStreakOnce()?.let { s ->
                _streakDaysCount.intValue = s.count
            }
        }
    }

    fun toggleTask(task: ChecklistEntity) {
        viewModelScope.launch {
            checklistDao.updateTask(task.copy(isCompleted = !task.isCompleted))
            updateStreakLogic()
        }
    }

    private suspend fun updateStreakLogic() {
        val allTasksList = checklistDao.getTasksOnce()
        if (allTasksList.isEmpty()) return

        val allDone = allTasksList.all { it.isCompleted }
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)

        if (allDone) {
            val current = streakDao.getStreakOnce() ?: StreakEntity(id = 1, count = 0, lastCheckDate = 0L)
            if (current.lastCheckDate < today) {
                val newCount = if (current.lastCheckDate == today - 1) current.count + 1 else 1
                val updatedStreak = current.copy(count = newCount, lastCheckDate = today)
                streakDao.updateStreak(updatedStreak)
                _streakDaysCount.intValue = newCount
            }
        }
    }

    fun addTask(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { checklistDao.addTask(ChecklistEntity(taskName = name)) }
    }

    fun deleteTask(task: ChecklistEntity) {
        viewModelScope.launch { checklistDao.deleteTask(task) }
    }
}