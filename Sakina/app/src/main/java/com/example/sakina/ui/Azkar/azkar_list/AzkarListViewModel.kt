package com.example.sakina.ui.Azkar.azkar_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sakina.data.repository.AzkarRepository
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository

) : ViewModel() {
    var selectedFilterId = mutableStateOf<String?>(null)

    fun onFilterChanged(newId: String?) {
        selectedFilterId.value = newId
    }
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val data = repository.getAllCategories()
            _categories.value = data
        }
    }
}