package com.sama.sakina.ui.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sama.sakina.data.local.database.dao.ChecklistDao
import com.sama.sakina.data.local.database.entity.ChecklistEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistDao: ChecklistDao
) : ViewModel() {

    val tasks: StateFlow<List<ChecklistEntity>> =
        checklistDao.getAllTasks()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // Streak بسيطة: بتحسب أيام متتالية فيها Task واحدة على الأقل completed
    val streakDays: StateFlow<Int> =
        tasks.map { list -> computeStreakFromCompletions(list) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleTask(task: ChecklistEntity) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val willBeCompleted = !task.isCompleted
            checklistDao.updateTask(
                task.copy(
                    isCompleted = willBeCompleted,
                    completedAt = if (willBeCompleted) now else null,
                    updatedAt = now
                )
            )
        }
    }

    // متوافق مع القديم
    fun addTask(name: String) = addTask(name, "عام")

    // الجديد
    fun addTask(name: String, category: String) {
        val clean = name.trim()
        if (clean.isBlank()) return

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val nextOrder = (tasks.value.maxOfOrNull { it.sortOrder } ?: 0) + 1

            checklistDao.addTask(
                ChecklistEntity(
                    taskName = clean,
                    category = category.trim().ifBlank { "عام" },
                    sortOrder = nextOrder,
                    isCompleted = false,
                    completedAt = null,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun deleteTask(task: ChecklistEntity) {
        viewModelScope.launch { checklistDao.deleteTask(task) }
    }

    fun editTask(task: ChecklistEntity, newName: String) {
        val clean = newName.trim()
        if (clean.isBlank()) return
        viewModelScope.launch { checklistDao.updateTaskName(task.id, clean) }
    }

    fun setCategory(task: ChecklistEntity, category: String) {
        val clean = category.trim().ifBlank { "عام" }
        viewModelScope.launch { checklistDao.updateTaskCategory(task.id, clean) }
    }

    fun deleteCompleted() {
        viewModelScope.launch { checklistDao.deleteCompletedTasks() }
    }

    fun moveUp(task: ChecklistEntity) {
        val list = tasks.value
        val idx = list.indexOfFirst { it.id == task.id }
        if (idx <= 0) return
        val above = list[idx - 1]
        swapOrder(task, above)
    }

    fun moveDown(task: ChecklistEntity) {
        val list = tasks.value
        val idx = list.indexOfFirst { it.id == task.id }
        if (idx == -1 || idx >= list.lastIndex) return
        val below = list[idx + 1]
        swapOrder(task, below)
    }

    private fun swapOrder(a: ChecklistEntity, b: ChecklistEntity) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            checklistDao.updateTaskOrder(a.id, b.sortOrder, now)
            checklistDao.updateTaskOrder(b.id, a.sortOrder, now)
        }
    }

    private fun computeStreakFromCompletions(list: List<ChecklistEntity>): Int {
        val zone = ZoneId.systemDefault()
        val completedDays: Set<LocalDate> = list.mapNotNull { it.completedAt }
            .map { millis -> Instant.ofEpochMilli(millis).atZone(zone).toLocalDate() }
            .toSet()

        if (completedDays.isEmpty()) return 0

        var streak = 0
        var day = LocalDate.now(zone)

        if (!completedDays.contains(day)) day = day.minusDays(1)

        while (completedDays.contains(day)) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}