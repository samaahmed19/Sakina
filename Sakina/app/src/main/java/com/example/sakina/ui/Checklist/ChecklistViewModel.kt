package com.example.sakina.ui.Checklist

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

    fun addNewTask(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                checklistDao.addTask(ChecklistEntity(taskName = name))
            }
        }
    }
    fun toggleTask(task: ChecklistEntity) {
        viewModelScope.launch {
            checklistDao.updateTaskStatus(task.copy(isCompleted = !task.isCompleted))
        }
    }
    fun removeTask(task: ChecklistEntity) {
        viewModelScope.launch {
            checklistDao.deleteTask(task)
        }
    }
}